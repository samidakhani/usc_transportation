SET DEFINE ON
DROP TABLE STUDENT;
DROP TABLE BUILDING;
DROP TABLE TRAMSTOP;

DROP INDEX STUDENT_SPATIAL_IDX;
DROP INDEX BUILDING_SPATIAL_IDX;
DROP INDEX TRAMSTOP_SPATIAL_IDX;

DELETE FROM USER_SDO_GEOM_METADATA
WHERE TABLE_NAME='STUDENT';
 
DELETE FROM USER_SDO_GEOM_METADATA
WHERE TABLE_NAME='BUILDING';
 
DELETE FROM USER_SDO_GEOM_METADATA
WHERE TABLE_NAME='TRAMSTOP';