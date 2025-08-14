# springboot-jpa-template
JPA 사용 템플릿

- Java 17
- Sprintboot 3.2.5
- Gradle 8.2
- MySQL 9.3.*
- Redis (Docker)
- JWT


## 빌드


- 프로젝트 루트 경로 이동

- 최초 빌드

> ./gradlew wrapper --gradle-version 8.2

- 빌드

> MAC : ./gradlew clean build

> WIN : gradlew clean build


## Redis

> docker run --name redis-local -p 6380:6379 -d redis:latest

## DB

> Mac 

```
docker run -d \
  --name mysql-dev \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -e MYSQL_DATABASE=mydb \
  -p 3306:3306 \
  -v $(pwd)/data.sql:/docker-entrypoint-initdb.d/data-Dev.sql:ro \
  mysql:9.3
```

> WIN

```
docker run -d `
  --name mysql-dev `
  -e MYSQL_ROOT_PASSWORD=root_password `
  -e MYSQL_DATABASE=mydb `
  -p 3306:3306 `
  -v ${PWD}\data.sql:/docker-entrypoint-initdb.d/data-Dev.sql:ro `
  mysql:9.3
```

### 접속

> docker exec -it mysql-dev mysql -uroot -proot_password mydb

