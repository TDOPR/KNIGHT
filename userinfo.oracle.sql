-- 菜单初始SQL
INSERT INTO sys_menu(id, pid, name, url, permissions, type, icon, sort, creator, create_date, updater, update_date)VALUES (1569618133584580610, 1067246875800000035, '', 'demo/userinfo', NULL, 0, 'icon-desktop', 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, name, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1569618133584580611, 1569618133584580610, '查看', NULL, 'demo:userinfo:page,demo:userinfo:info', 1, NULL, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, name, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1569618133584580612, 1569618133584580610, '新增', NULL, 'demo:userinfo:save', 1, NULL, 1, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, name, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1569618133584580613, 1569618133584580610, '修改', NULL, 'demo:userinfo:update', 1, NULL, 2, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, name, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1569618133584580614, 1569618133584580610, '删除', NULL, 'demo:userinfo:delete', 1, NULL, 3, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, name, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1569618133584580615, 1569618133584580610, '导出', NULL, 'demo:userinfo:export', 1, NULL, 4, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
