

-- Community Basic Vocabulary
INSERT INTO `community_vocabulary` (community_id,guilanguage_id,communityName,communityComment,communityVocabularyDescription,elementTraduction,elementsTraduction,datasetTraduction,datasetsTraduction,metadataTraduction,metadatasTraduction,catalogTraduction,catalogsTraduction,userTraduction,usersTraduction,userGroupTraduction,userGroupsTraduction) VALUES
(%community_id%,(select guilanguage_id from guilanguages where shortname="EN"),
			'Community Name','some comments',
			'This community lets its %users% to organize and consult %elements%. Each %element% is made of several %metadatas%, and %elements% can be grouped within %catalogs%.',
			'study element','study elements',
			'dataset','datasets','metadata','metadatas','catalog','catalogs',
			'member','members','group','groups'),
(%community_id%,(select guilanguage_id from guilanguages where shortname="FR"),
			'Votre Communauté','une petite description',
			'Cette communauté permet à ses %users% d organiser et consulter des %elements%. Chaque %element% est fait de %metadatas%, et ces %elements% peuvent être groupés par %catalogs%.',
			'objet','objets',
			'dataset','datasets','métadonnée','métadonnées','catalogue','catalogues',
			'membre','membres','groupe','groupes'),
(%community_id%,(select guilanguage_id from guilanguages where shortname="SP"),
			'Nombre de la Comunidad','una explicacion',
			'Este comunidad piermete a su %users% de organisar y consultar %elements%. Cada %element% tiene %metadatas%, y estos %elements% pse pueden regroupar en %catalogs%.',
			'objecto','objectos',
			'dataset','datasets','metadata','metadatas','catalog','catalogs',
			'user','users','groupo','groupos');

-- Community default Terms definition
INSERT INTO `community_terms` (community_id,datatype_id,idName) VALUES
(%community_id%,(select datatype_id from datatypes where name="TinyText"),"Title"),
(%community_id%,(select datatype_id from datatypes where name="TinyText"),"Author");

-- Community Default terms traductions
INSERT INTO `community_terms_vocabulary` (guilanguage_id,community_term_id,termNameTraduction,termCommentTraduction) VALUES

(				(select guilanguage_id from guilanguages where shortname="EN"),
				(select community_term_id from community_terms where idName="Title" and community_id='%community_id%'),
				"Title","The title of the element"),
(				(select guilanguage_id from guilanguages where shortname="EN"),
				(select community_term_id from community_terms where idName="Author" and community_id='%community_id%'),
				"Author","The author of the element"),	
(				(select guilanguage_id from guilanguages where shortname="FR"),
				(select community_term_id from community_terms where idName="Title" and community_id='%community_id%'),
				"Titre","Le titre de l\'élément"),
(				(select guilanguage_id from guilanguages where shortname="FR"),
				(select community_term_id from community_terms where idName="Author" and community_id='%community_id%'),
				"Auteur","L\'auteur de l\'élément"),	
(				(select guilanguage_id from guilanguages where shortname="SP"),
				(select community_term_id from community_terms where idName="Title" and community_id='%community_id%'),
				"Titulo","El titulo del elemento"),
(				(select guilanguage_id from guilanguages where shortname="SP"),
				(select community_term_id from community_terms where idName="Author" and community_id='%community_id%'),
				"Autor","El autor del elemento") ;

				
-- Community Default Group : admin, observers, workers
INSERT INTO `community_groups` (community_id,name,comment) VALUES
(%community_id%,"Admin","Can create,modify,delete any data of the community, and manage groups and users"),
(%community_id%,"Observers","Can only have a visualize data, but cannot do any modification"),
(%community_id%,"Workers","Can create data, and modify,delete data he owns");

-- Set creator user as Admin
INSERT INTO `community_usergroups` (user_id,community_group_id) VALUES
(		(select user_id from users where username='%user_id%'),
		(select community_group_id from community_groups where name="Admin" and community_id='%community_id%'));
