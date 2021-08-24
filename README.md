# aws-firelens-logrewriter
Rewrite the aws firelens log.

# updates
A native version based on quarkus is provided. see subfolder [aws-firelens-logrewriter-native](aws-firelens-logrewriter-native/README.md)
This version gives better performance.

# Why?
The aws ecs has a componet named [firelens](https://aws.amazon.com/about-aws/whats-new/2019/11/aws-launches-firelens-log-router-for-amazon-ecs-and-aws-fargate/) which you can rewrite the logs to other components. 
but the log formart has additional ecs related properties which I dodn't need it. <br>
An example record is:
```
{
	"container_id": "82b1fdfa1ca4d69cf8f19.....6c857c",
	"container_name": "/ecs-sxxxxx00",
	"source": "stdout",
	"log": "08-20 02:12:57.023.+0000 [INFO ][http-nio-7070-exec-1][c.f.i.w.c.MetricController.scrape:20]Metrics received 16247",
	"ec2_instance_id": "i-xxxxxxx",
	"ecs_cluster": "xxxx-stage-cluster",
	"ecs_task_arn": "arn:aws:ecs:us-west-2:zzzzzz",
	"ecs_task_definition": "task-definition:5"
}

```

What I need is the "log" part. I didn't use the fluentbit/fluentd. because it's not simple to control.

# This project 
This project will support receive the loki body and process it and resend to another loki.
The process is removing additional invalid properties. and it will preserve the loki labels.

# Gradle Build

# Env variables
It requires the following environment variables:
``
LOKI_HOST  default: 127.0.0.1
LOKI_PORT  default: 3100
``
by defualt it's listening on port 24223

# java version 

# docker version
``
an example:
docker run --restart always -e LOKI_HOST=172.10.10.10 --name logrewriter -d -p 24223:24223 edwardg/aws-firelens-logrewriter:latest
``
