# Spring-Cloud-Data-Fow

sudo service docker start
docker exec -t -i dataflow-server /bin/bash
docker exec -it dataflow-server java -jar shell.jar

Upload the jar from local to server using winscp

app register --type task --name data-injection-task --uri file://root/apps/data-injection-0.0.1-SNAPSHOT.jar
app register --type task --name transform-task --uri file://root/apps/transformation-0.0.1-SNAPSHOT.jar
app register --type task --name outbound-task --uri file://root/apps/outbound-0.0.1-SNAPSHOT.jar

app.input=https://scdf-microservices.s3.amazonaws.com/input.csv
app.composite-task-7.outbound-task.app.output=/root/apps/output.csv