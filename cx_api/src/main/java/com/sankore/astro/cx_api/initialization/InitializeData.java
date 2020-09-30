package com.sankore.astro.cx_api.initialization;

import com.sankore.astro.entity.*;
import com.sankore.astro.enums.*;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author Obi on 22/05/2019
 */
@Component
public class InitializeData {

    @Autowired
    Environment env;

    @Autowired
    AstroLoggerService log;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    IssueCategoryRepository issueCategoryRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostConstruct
    private void onApplicationStartup() {
        //This has to be created in this order
        initIssueCategories();
        initDefaultMenu();
        initDefaultRolesIfRequired();
        initDefaultManagementAdminIfRequired();
        mapPermissionsToRoles();
    }

    private boolean initIssueCategories() {
        List<IssueCategory> categories = new ArrayList<>();
        try {
            if (issueCategoryRepository.findAll().isEmpty()) {
                for (TicketCategory ticketCategory : TicketCategory.values()) {
                    IssueCategory category = new IssueCategory();
                    category.setName(ticketCategory.name());
                    category.setDescription(ticketCategory.name());
                    categories.add(category);
                }
                issueCategoryRepository.saveAll(categories);
            }
        } catch (Exception ex) {
            log.error(env.getProperty("categories.init.error"), ex);
        }
        return !categories.isEmpty() && categories.size() == TicketCategory.values().length;
    }

    //Menu
    private boolean initDefaultMenu() {
        List<Menu> menus = new ArrayList<>();
        try {
            if (menuRepository.findAll().isEmpty()) {
                for (DefaultMenu defaultMenu : DefaultMenu.values()) {
                    Menu menu = new Menu();
                    menu.setName(defaultMenu.getDisplayName());
                    menu.setPosition(defaultMenu.getPosition());
                    menu.setUrl(defaultMenu.getUrl());
                    menu.setActive(true);
                    StringBuilder commands = new StringBuilder();
                    DefaultCommand.findByMenu(defaultMenu.name()).stream().forEach(x -> commands.append(x.name() + ", "));
                    menu.setCommands(commands.toString());
                    menus.add(menu);
                }
                menuRepository.saveAll(menus);
            }
        } catch (Exception ex) {
            log.error(env.getProperty("menu.init.error"), ex);
        }
        return !menus.isEmpty() && menus.size() == DefaultMenu.values().length;
    }

    //Role
    private boolean initDefaultRolesIfRequired() {
        List<Role> roles = new ArrayList<>();
        try {
            if (roleRepository.findAll().isEmpty()) {
                for (RoleType roleType : RoleType.values()) {
                    Role role = new Role();
                    role.setName(roleType.getScreenName());
                    role.setSystem(true);
                    role.setActive(true);
                    roles.add(role);
                }
                roleRepository.saveAll(roles);
                mapRolesToMenu(roles);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(env.getProperty("role.init-error"), ex);
        }
        return !roles.isEmpty() && roles.size() == RoleType.values().length;
    }

    //Role-Menu-Map
    private boolean mapRolesToMenu(List<Role> roles) {
        if (roles == null || roles.isEmpty())
            return false;

        try {
            for (Role role : roles) {
                Set<Menu> menus = new HashSet<>();
                DefaultMenu.findByRole(role.getName()).stream().forEach(x -> menus.add(menuRepository.findMenuByUrl(x.getUrl())));
                if (!menus.isEmpty()) {
                    role.setMenus(menus);
                }
                roleRepository.save(role);
            }
            return true;
        } catch (Exception ex) {
            log.error(env.getProperty("role-menu-map.init.error"), ex);
            return false;
        }
    }

    private boolean initDefaultManagementAdminIfRequired() {
        Client client = new Client();
        User user = new User();
        try {
            if (!clientRepository.existsByCode(ManagementInfo.CLIENT_SERVICE_CODE)) {
                client.setName(ManagementInfo.CLIENT_NAME);
                client.setShortName(ManagementInfo.CLIENT_SHORT_NAME);
                client.setCode(ManagementInfo.CLIENT_SERVICE_CODE);
                client.setEmail(ManagementInfo.EMAIL);
                client.setLogo("");
                client.setActive(true);
                client.setSystemDefined(true);
                client.setLogo("https://dp76jxyzopnbo.cloudfront.net/wealthng/upload/wealth_admin/45020974358263sankore.png");
                client = clientRepository.save(client);

                if (client != null) {
                    if (userRepository.findAll().isEmpty()) {
                        user.setEmailAddress(env.getProperty("astrocx.default.username"));
                        user.setFirstName(ManagementInfo.FIRST_NAME);
                        user.setLastName(ManagementInfo.LAST_NAME);
                        user.setPassword(passwordEncoder.encode(env.getProperty("astrocx.default.password")));
                        user.setActive(true);
                        user.setDateCreated(new Date());
                        user.setClient(client);
                        user.getUserRoles().add(roleRepository.findRoleByName(RoleType.SUPER_ADMIN.getScreenName()));
                        user = userRepository.save(user);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(env.getProperty("management-admin.init-error"), ex);
        }
        return client.getId() != null && user.getId() != null;
    }

    private boolean mapPermissionsToRoles() {
        try {
            for (RoleType roleType : RoleType.values()) {
                Set<Menu> menus = new HashSet<>();

                Role role = roleRepository.findRoleByName(roleType.getScreenName());
                if (permissionRepository.findAllByRole_Id(role.getId()).isEmpty()) {
                    DefaultMenu.findByRole(roleType.getScreenName()).stream().forEach(x -> menus.add(menuRepository.findMenuByUrl(x.getUrl())));

                    Set<Permission> permissions = new HashSet<>();
                    if (!CollectionUtils.isEmpty(menus)) {
                        for (Menu menu : menus) {
                            Permission permission = new Permission();
                            permission.setCommands(menu.getCommands());
                            permission.setMenu(menu);
                            permission.setRole(role);
                            permissions.add(permission);
                        }
                        permissionRepository.saveAll(permissions);

                        if (role.getName().equals(RoleType.SUPER_ADMIN.getScreenName())) {
                            role.setClient(clientRepository.findByCode(ManagementInfo.CLIENT_SERVICE_CODE));
                            roleRepository.save(role);
                        }
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            log.error(env.getProperty("user-permission-map.init-error"), ex);
        }
        return false;
    }

}
