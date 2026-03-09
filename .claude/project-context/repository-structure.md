Repository Structure

Base package: `com.example.template`

src/main/java/com/example/template/
  Application.java
  auth/           # sign-in, sign-out, refresh-token
  admin/          # admin CRUD, list (Controller, Service, Repository)
  config/         # Security, DataSource, QueryDSL, JWT, Redis
  security/       # TokenProvider, SecurityConstants, CustomAuthenticationProvider
  filter/         # JwtRequestFilter
  model/          # BaseEntity, entities, RoutingDataSource
  exception/      # GlobalExceptionHandler, BusinessException, JWT entry/denied handlers
  redis/          # RedisService
  refresh/        # RefreshToken repository
  common/         # ApiResponse, DTOs, paging
  constants/      # ResponseCode, ApprovalStatus, AuthConstants
  error/          # FailResponse, CustomErrorController
  aop/            # LogAspect
  util/

src/main/resources/
  application.yml, application-{profile}.yml
  data.sql

src/main/generated/   # QueryDSL Q-classes (do not edit)

src/test/
  java/com/example/template/
    ApplicationTests.java
    auth/
      AuthControllerTest.java
      AuthServiceImplTest.java
    admin/
      AdminControllerTest.java
      AdminServiceImplTest.java
    menu/
      MenuControllerTest.java
      MenuServiceImplTest.java
    authority/
      AuthorityGroupControllerTest.java
      AuthorityGroupServiceImplTest.java
