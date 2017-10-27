
--
-- Structure for table stationnement_nofeesdays
--

DROP TABLE IF EXISTS stationnement_nofeesdays;
CREATE TABLE stationnement_nofeesdays (
id_no_fees_day int AUTO_INCREMENT,
date date NOT NULL,
comment varchar(255) default '',
PRIMARY KEY (id_no_fees_day)
);

