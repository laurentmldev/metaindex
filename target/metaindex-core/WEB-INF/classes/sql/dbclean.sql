SET FOREIGN_KEY_CHECKS=0;

-- generic tables
DROP TABLE if exists user_roles, communities, datatypes, users,guilanguages, guithemes;

-- community tables
DROP TABLE if exists community_elements,community_templates,community_vocabulary, community_terms,community_groups,community_usergroups,community_terms_vocabulary,community_catalogs,community_static_catalogs_elements;
DROP TABLE if exists community_datasets,community_metadata;
DROP TABLE if exists community_access_elements;

SET FOREIGN_KEY_CHECKS=1;
