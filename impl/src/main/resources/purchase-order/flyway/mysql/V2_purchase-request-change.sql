DROP INDEX IDXgr1ltgh31tsq4dlp02u68v7yg ON pco_purchase_order_item;
ALTER TABLE pco_purchase_order_item CHANGE request_item_id request_id binary(16);
ALTER TABLE pco_purchase_order_item ADD item_spec_code varchar(20);

create index IDXpb9c0x14g2dxtn4pcv930bat8
	on pco_purchase_order_item (request_id);
