DELETE FROM configuration where true;


INSERT INTO configuration (key, value, version, created_at, modified_at, created_by, modified_by) VALUES ('proposal.chain', '{"create": ["CREATE_PROPOSAL", "VALIDATE_PROPOSAL", "COMMISSIONS_PROPOSAL", "BASIC_DATA_PROPOSAL", "MODIFY_TAXES_SPREADS_DYNAMIC_CONDITIONS", "TAXES_AND_SPREADS_DYNAMIC_CONDITIONS", "MODIFY_TERMS_CONDITIONS", "MODIFY_GENERAL_TERMS_CONDITIONS", "MODIFY_GENERAL_CONDITIONS", "GENERAL_CONDITIONS", "PERIODIC_COMMISSIONS", "MODIFY_PERIODIC_COMMISSIONS", "CREATE_FINANCIAL_ASSETS", "CONSTITUTE_GUARANTEE", "ASSOCIATE_GUARANTEES", "ASSOCIATE_GUARANTEES_TO_PROPOSAL", "CREATE_COVENANTS", "CONSULT_COVENANTS", "ASSOCIATE_INSURANCES"]}', 1, now(), now(), 'System', 'System');
INSERT INTO configuration (key, value, version, created_at, modified_at, created_by, modified_by) VALUES ('operation.type', '{"operation": [{"key": ["301-152", "301-007", "301-208", "301-206", "301-209", "301-207", "301-134", "300-002", "300-058"], "type": "04"}, {"key": ["303-004", "303-027", "303-036"], "type": "13"}]}', 1, now(), now(), 'System', 'System');
INSERT INTO configuration (key, value, version, created_at, modified_at, created_by, modified_by) VALUES ('periodicityType.mapping', '{"periodicityType": [{"key": ["0266"], "type": "9"}, {"key": ["0265"], "type": "8"}, {"key": ["0264", "1100"], "type": "6"}, {"key": ["0263"], "type": "4"}]}', 1, now(), now(), 'System', 'System');
INSERT INTO configuration (key, value, version, created_at, modified_at, created_by, modified_by) VALUES ('periodicity.mapping', '{"periodicity": [{"key": ["0266", "0265", "0264", "0263", "1100"], "type": "1"}]}', 1, now(), now(), 'System', 'System');
INSERT INTO configuration (key, value, version, created_at, modified_at, created_by, modified_by) VALUES ('family.products.to.change.commission', '{"commissionsMap": [{"event": "0200", "subEvent": "0003", "newSubEvent": "0014", "familyAndProduct": ["300-002", "300-058"]}]}', 1, now(), now(), 'System', 'System');
INSERT INTO configuration (key, value, version, created_at, modified_at, created_by, modified_by) VALUES ('operation.flow', '{"operation": [{"key": ["301-152", "301-007", "301-208", "301-206", "301-209", "301-207", "301-134", "300-002", "300-058"], "type": "CCE"}, {"key": ["303-004", "303-027", "303-036"], "type": "CCA"}]}', 1, now(), now(), 'System', 'System');
INSERT INTO configuration (key, value, version, created_at, modified_at, created_by, modified_by) VALUES ('periodic.commissions', '{"commissions": [{"code": "155", "event": "0254", "subEvent": "0001"}, {"code": "156", "event": "0254", "subEvent": "0002"}]}', 1, now(), now(), 'System', 'System');



with existing as (
    select key, cast(value as jsonb) as value
    from configuration
)
update configuration set value = e.value
from existing e where e.key = configuration.key;