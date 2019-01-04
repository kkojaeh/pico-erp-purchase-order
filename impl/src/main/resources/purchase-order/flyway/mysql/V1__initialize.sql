create table pco_purchase_order (
	id binary(16) not null,
	canceled_date datetime,
	charger_id varchar(50),
	code varchar(20),
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	determined_date datetime,
	due_date datetime,
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	receive_address_detail varchar(50),
	receive_address_postal_code varchar(10),
	receive_address_street varchar(50),
	received_date datetime,
	receiver_id varchar(50),
	rejected_date datetime,
	rejected_reason varchar(50),
	remark varchar(50),
	sent_date datetime,
	status varchar(20),
	supplier_id varchar(50),
	primary key (id)
) engine=InnoDB;

create table pco_purchase_order_item (
	id binary(16) not null,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	estimated_unit_cost decimal(19,2),
	item_id binary(16),
	item_spec_id binary(16),
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	order_id binary(16),
	project_id binary(16),
	quantity decimal(19,2),
	received_quantity decimal(19,2),
	remark varchar(50),
	request_item_id binary(16),
	status varchar(20),
	unit varchar(20),
	unit_cost decimal(19,2),
	primary key (id)
) engine=InnoDB;

create index IDX3y1agytor6hbsu9dvj0amuymg
	on pco_purchase_order_item (order_id);

create index IDXgr1ltgh31tsq4dlp02u68v7yg
	on pco_purchase_order_item (request_item_id);
