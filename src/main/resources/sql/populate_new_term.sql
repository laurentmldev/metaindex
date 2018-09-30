-- Term Basic Vocabulary
INSERT INTO `community_terms_vocabulary` (community_term_id,guilanguage_id,termNameTraduction,termCommentTraduction) VALUES
(%term_id%,(select guilanguage_id from guilanguages where shortname="EN"),
			'Term Name','some description'),
(%term_id%,(select guilanguage_id from guilanguages where shortname="FR"),
			'Nom du Terme','une petite description'),
(%term_id%,(select guilanguage_id from guilanguages where shortname="SP"),
			'Nombre del Campo','una explicacion');
