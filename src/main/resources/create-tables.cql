CREATE TABLE IF NOT EXISTS accounts_by_user (
  username text,
  account_number text,
  cash_balance decimal,
  name text STATIC,
  PRIMARY KEY (username, account_number)
);

CREATE TABLE IF NOT EXISTS positions_by_account (
  account text,
  symbol text,
  quantity decimal,
  PRIMARY KEY (account, symbol)
);