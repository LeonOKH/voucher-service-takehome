INSERT INTO campaign (id, name, client_code, total_stock, remaining_stock, active) VALUES
  (1, 'Raya Flash Deal', 'ACME', 100, 100, TRUE),
  (2, 'Merdeka Giveaway', 'NOVA', 50, 50, TRUE),
  (3, 'Expired Test Campaign', 'ACME', 10, 0, FALSE);

INSERT INTO voucher (id, campaign_id, code, status, redeemed_by, redeemed_at) VALUES
  (1, 1, 'RAYA-0001', 'ACTIVE',   NULL,      NULL),
  (2, 1, 'RAYA-0002', 'ACTIVE',   NULL,      NULL),
  (3, 1, 'RAYA-0003', 'ACTIVE',   NULL,      NULL),
  (4, 1, 'RAYA-0004', 'REDEEMED', 'user-99', '2026-06-01 10:15:00'),
  (5, 1, 'RAYA-0005', 'VOID',     'user-99', '2026-06-02 09:40:00'),
  (6, 1, 'RAYA-0006', 'ACTIVE',   NULL,      NULL),
  (7, 2, 'MRDK-0001', 'ACTIVE',   NULL,      NULL),
  (8, 2, 'MRDK-0002', 'ACTIVE',   NULL,      NULL),
  (9, 3, 'EXPD-0001', 'ACTIVE',   NULL,      NULL);

INSERT INTO redemption (voucher_id, campaign_id, user_id, created_at) VALUES
  (4, 1, 'user-99', '2026-06-01 10:15:00'),
  (5, 1, 'user-99', '2026-06-02 09:40:00');
