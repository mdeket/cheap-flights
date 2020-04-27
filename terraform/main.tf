provider "aws" {
  version = "~> 2.0"
  region  = "eu-central-1"
  profile = "htec"
}

resource "aws_vpc" "htec-vpc" {
  cidr_block       = "172.31.0.0/16"
  instance_tenancy = "default"
  enable_dns_hostnames = true

  tags = {
    Name = "htec-vpc"
  }
}

resource "aws_subnet" "htec-subnet-public-1c" {
  vpc_id     = aws_vpc.htec-vpc.id
  cidr_block = "172.31.0.0/20"
  map_public_ip_on_launch = true
  availability_zone = "eu-central-1c"

  tags = {
    Name = "htec-subnet-public-1c"
  }
}

resource "aws_subnet" "htec-subnet-public-1b" {
  vpc_id     = aws_vpc.htec-vpc.id
  cidr_block = "172.31.16.0/20"
  map_public_ip_on_launch = true
  availability_zone = "eu-central-1b"

  tags = {
    Name = "htec-subnet-public-1b"
  }
}

resource "aws_network_interface" "test" {
  subnet_id       = aws_subnet.htec-subnet-public-1c.id
  private_ips     = ["172.31.0.50"]
  security_groups = [aws_security_group.htec-lb-sg.id]
}

resource "aws_security_group" "htec-lb-sg" {
  name        = "allow_tls"
  description = "Allow TLS inbound traffic"
  vpc_id      = aws_vpc.htec-vpc.id

  ingress {
    description = "HTTP from VPC"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.htec-vpc.cidr_block]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "htec-backend-sg" {
  name        = "htec-backend-sg"
  description = "Allows traffic on port 8080."
  vpc_id      = aws_vpc.htec-vpc.id

  ingress {
    description = "HTTP from VPC"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.htec-vpc.cidr_block]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_internet_gateway" "htec-gw" {
  vpc_id = aws_vpc.htec-vpc.id
}


resource "aws_route_table" "htec-rt" {
  vpc_id = aws_vpc.htec-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.htec-gw.id
  }

  tags = {
    Name = "htec-rt"
  }
}

resource "aws_lb" "htec-lb" {
  name               = "htec-lb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.htec-lb-sg.id, aws_security_group.htec-backend-sg.id]
  subnets            = [aws_subnet.htec-subnet-public-1c.id, aws_subnet.htec-subnet-public-1b.id]
  enable_deletion_protection = false

  tags = {
    Environment = "production"
  }
}

resource "aws_lb_target_group" "htec-backend-tg" {
  name        = "htec-backend-tg"
  port        = 80
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = aws_vpc.htec-vpc.id
  health_check {
    protocol = "http"
    path = "/api/v1/cities"
    port = "traffic-port"
    healthy_threshold = 5
    unhealthy_threshold = 2
    timeout = 5
    interval = 30
    matcher = "200-499"
  }
}

resource "aws_lb_listener" "htec-lb-backend-tg" {
  load_balancer_arn = aws_lb.htec-lb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.htec-backend-tg.arn
  }
}

resource "aws_lb_listener" "htec-lb-backend-2-tg" {
  load_balancer_arn = aws_lb.htec-lb.arn
  port              = "8080"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.htec-backend-tg.arn
  }
}
