-- ------------------------------------------------ Foreign Keys -----------------------------------------------
ALTER TABLE actor ADD CONSTRAINT fk_actor_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE actormember ADD CONSTRAINT fk_actormember_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE actormember ADD CONSTRAINT fk_actormember_actorId FOREIGN KEY (actorId, tenantid) REFERENCES actor(id, tenantid);
ALTER TABLE breakpoint ADD CONSTRAINT fk_breakpoint_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
-- ALTER TABLE queriable_log ADD CONSTRAINT fk_queriable_log_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE queriablelog_p ADD CONSTRAINT fk_queriablelog_p_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE category ADD CONSTRAINT fk_category_tenantId FOREIGN KEY (tenantid) REFERENCES tenant (id);
ALTER TABLE command ADD CONSTRAINT fk_command_tenantId FOREIGN KEY (tenantid) REFERENCES tenant (id);
ALTER TABLE connector_instance ADD CONSTRAINT fk_connector_instance_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE data_instance ADD CONSTRAINT fk_data_instance_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE data_mapping ADD CONSTRAINT fk_data_mapping_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE datasource ADD CONSTRAINT fk_datasource_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE datasourceparameter ADD CONSTRAINT fk_datasourceparameter_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE dependency ADD CONSTRAINT fk_dependency_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE dependencymapping ADD CONSTRAINT fk_dependencymapping_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE document_content ADD CONSTRAINT fk_document_content_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE document_mapping ADD CONSTRAINT fk_document_mapping_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE event_trigger_instance ADD CONSTRAINT fk_event_trigger_instance_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE external_identity_mapping ADD CONSTRAINT fk_external_identity_mapping_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE flownode_instance ADD CONSTRAINT fk_flownode_instance_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE group_ ADD CONSTRAINT fk_group__tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE hidden_activity ADD CONSTRAINT fk_hidden_activity_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE job_desc ADD CONSTRAINT fk_job_desc_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE job_param ADD CONSTRAINT fk_job_param_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE message_instance ADD CONSTRAINT fk_message_instance_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
-- ALTER TABLE migration_plan ADD CONSTRAINT fk_migration_plan_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE custom_usr_inf_def ADD CONSTRAINT fk_custom_usr_inf_def_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE custom_usr_inf_val ADD CONSTRAINT fk_custom_usr_inf_val_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE pending_mapping ADD CONSTRAINT fk_pending_mapping_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE pending_mapping ADD CONSTRAINT fk_pending_mapping_flownode_instanceId FOREIGN KEY (activityId, tenantid) REFERENCES flownode_instance(id, tenantid);
ALTER TABLE processcategorymapping ADD CONSTRAINT fk_processcategorymapping_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE process_comment ADD CONSTRAINT fk_process_comment_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE process_definition ADD CONSTRAINT fk_process_definition_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE process_instance ADD CONSTRAINT fk_process_instance_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE processsupervisor ADD CONSTRAINT fk_processsupervisor_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE profile ADD CONSTRAINT fk_profile_tenantId FOREIGN KEY (tenantId) REFERENCES tenant(id);
ALTER TABLE profileentry ADD CONSTRAINT fk_profileentry_tenantId FOREIGN KEY (tenantId) REFERENCES tenant(id);
ALTER TABLE profilemember ADD CONSTRAINT fk_profilemember_tenantId FOREIGN KEY (tenantId) REFERENCES tenant(id);
ALTER TABLE ref_biz_data_inst ADD CONSTRAINT fk_ref_biz_data_inst_tenantId FOREIGN KEY (tenantId) REFERENCES tenant(id);
ALTER TABLE role ADD CONSTRAINT fk_role_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE theme ADD CONSTRAINT fk_theme_tenantId FOREIGN KEY (tenantId) REFERENCES tenant(id);
ALTER TABLE user_ ADD CONSTRAINT fk_user__tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE user_membership ADD CONSTRAINT fk_user_membership_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE waiting_event ADD CONSTRAINT fk_waiting_event_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);

ALTER TABLE profileentry ADD CONSTRAINT fk_profileentry_profileId FOREIGN KEY (tenantId, profileId) REFERENCES profile(tenantId, id);
ALTER TABLE profilemember ADD CONSTRAINT fk_profilemember_profileId FOREIGN KEY (tenantId, profileId) REFERENCES profile(tenantId, id);
-- ALTER TABLE process_comment	ADD	CONSTRAINT fk_process_comment_process_instanceId FOREIGN KEY (processInstanceId, tenantid) REFERENCES process_instance(id, tenantid);


--  ------------------------ Foreign Keys to disable if archiving is on another BD ------------------
ALTER TABLE arch_document_mapping ADD CONSTRAINT fk_arch_document_mapping_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE arch_flownode_instance ADD CONSTRAINT fk_arch_flownode_instance_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE arch_process_comment ADD CONSTRAINT fk_arch_process_comment_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE arch_process_instance ADD CONSTRAINT fk_arch_process_instance_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE arch_transition_instance ADD CONSTRAINT fk_arch_transition_instance_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE arch_data_instance ADD CONSTRAINT fk_arch_data_instance_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
ALTER TABLE arch_data_mapping ADD CONSTRAINT fk_arch_data_mapping_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id);
