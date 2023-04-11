create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)                       null comment '用户昵称',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别
',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    userStatus   int      default 0                 not null comment '0 - 正常',
    userRole     int      default 0                 not null comment '用户权限 0 - 普通用户 1 - 管理员  ',
    planetCode   varchar(512)                       null comment '星球编号',
    email        varchar(512)                       null comment '邮箱',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '用户';

