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

Test Files

Auth
  test/auth/AuthControllerTest.java       — sign-up, sign-in, sign-out, refresh-token controller tests
  test/auth/AuthServiceImplTest.java      — signUp duplicate check, signOut, saveRefreshToken, validateRefreshToken

Admin
  test/admin/AdminControllerTest.java     — GET /user/v1|v2|v3, GET /user/{id} controller tests
  test/admin/AdminServiceImplTest.java    — getAdminListV1/V2/V3 (전체 + 조건검색), getAdminDetail

Menu
  test/menu/MenuControllerTest.java       — GET /menu, /menu/accessible, /menu/{id}/accessible
  test/menu/MenuServiceImplTest.java      — getAllMenus, getAccessibleMenus, checkMenuAccess

Authority
  test/authority/AuthorityGroupControllerTest.java    — group CRUD + group menu CRUD controller tests
  test/authority/AuthorityGroupServiceImplTest.java   — group CRUD + menu mapping service tests

Purpose

- Help AI find relevant files quickly.
- Reduce repository scanning.
