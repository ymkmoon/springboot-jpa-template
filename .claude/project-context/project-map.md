Project Map

Example structure mapping

Auth Feature

auth/AuthController.java
auth/AuthService.java, auth/AuthServiceImpl.java
security/TokenProvider.java
filter/JwtRequestFilter.java

Admin Feature

admin/AdminController.java
admin/AdminService.java, admin/AdminServiceImpl.java
admin/AdminRepository.java, admin/AdminRepositoryCustom.java
common/dto/AdminDto.java

Menu Feature

menu/MenuController.java
menu/MenuService.java, menu/MenuServiceImpl.java
menu/MenuRepositoryCustom.java
menu/MenuRepository.java
common/dto/MenuDto.java

Authority Feature

authority/AuthorityGroupController.java
authority/AuthorityGroupService.java, authority/AuthorityGroupServiceImpl.java
authority/AuthorityGroupRepository.java
authority/AuthorityGroupMenuRepository.java
authority/AuthorityLevelRepository.java
common/dto/AuthorityGroupDto.java
common/dto/AuthorityGroupMenuDto.java

Shared / Config

config/SecurityConfig.java
config/DataSourceConfig.java
exception/GlobalExceptionHandler.java
common/ApiResponse.java
model/BaseEntity.java

Purpose

- Help AI find relevant files quickly.
- Reduce repository scanning.
