CREATE TABLE `users` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT,
  `name`          VARCHAR(64)   NOT NULL,
  `email`         VARCHAR(254)  NOT NULL,
  `access_token`  VARCHAR(254),
  `token_type`    VARCHAR(254),
  `expires_in`    BIGINT,
  `refresh_token` VARCHAR(254),
  `create_at`     TIMESTAMP     NOT NULL DEFAULT current_timestamp,
  `update_at`     TIMESTAMP     NOT NULL DEFAULT current_timestamp,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC)
) ENGINE=InnoDB;