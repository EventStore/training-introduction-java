//// -- LEVEL 1
//// -- Tables and References

// Creating tables
Table users as U {
  id int [pk, increment] // auto-increment
  full_name varchar
  created_at timestamp
  address_id int
}

Table addresses as A {
  id int [pk, increment] // auto-increment
  house_number varchar
  street varchar
  zip_code varchar
  country_code int
}

Table merchants {
  id int [pk]
  merchant_name varchar
  country_code int
  "created at" varchar
  admin_id int [ref: > U.id] // inline relationship (many-to-one)
}

Table countries {
  code int [pk]
  name varchar
  continent_name varchar
 }

Table invoices as I {
  id int [pk]
  customer_id int [ref: > U.id]
  amount double
  issued_at varchar
  address_id int [ref: > A.id]
}

// Creating references
// You can also define relaionship separately
// > many-to-one;
< one-to-many;
- one-to-one
Ref: U.address_id > addresses.id
Ref: A.country_code > countries.code
Ref: merchants.country_code > countries.code

//----------------------------------------------//

//// -- LEVEL 2
//// -- Adding column settings

Table order_items {
  order_id int [ref: > orders.id]
  product_id int
  quantity int [default: 1] // default value
}

Ref: order_items.product_id > products.id

Table orders {
  id int [pk] // primary key
  user_id int [not null, unique]
  status varchar
  created_at varchar [note: 'When order created'] // add column note
  address_id int [ref: > A.id]
}

//----------------------------------------------//

//// -- Level 3
//// -- Enum, Indexes

// Enum for 'products' table below
Enum products_status {
  out_of_stock
  in_stock
  running_low [note: 'less than 20'] // add column note
}

// Indexes: You can define a single or multi-column index
Table products {
  id int [pk]
  name varchar
  merchant_id int [not null]
  price int
  status products_status
  created_at datetime [default: `now()`]

  Indexes {
    (merchant_id, status) [name:'product_status']
    id [unique]
  }
}

Ref: products.merchant_id > merchants.id // many-to-one


Ref: "orders"."user_id" < "users"."id"