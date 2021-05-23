update transactions t set nature = 1 where salt_edge_transaction_id is not null;

update transactions t set nature = 2
where (source_type = 'ExpenseSource' and destination_type = 'ExpenseCategory' and is_virtual_destination = true)
   or ((source_type = 'IncomeSource' or source_type = 'ExpenseSource') and destination_type = 'ExpenseSource' and is_virtual_source = true)
   or (credit_id is not null and is_virtual_destination = true)
   or (borrow_id is not null and (is_virtual_destination = true or is_virtual_source = true))
   or (active_id is not null and is_virtual_source = true);

update transactions t set purpose = 1 where credit_id is not null and borrow_id is not null or active_id is not null;

update transactions t set purpose = 1
from expense_sources es
where t.destination_id = es.id and t.source_type = 'ExpenseSource' and t.destination_type = 'ExpenseSource'
  and t.is_virtual_source = true and es.is_virtual = false and es.deleted_at is null and t.deleted_at is null
  and t.got_at = (select min(got_at) from transactions tr where (tr.destination_id = es.id and tr.destination_type='ExpenseSource') or (tr.source_id = es.id and tr.source_type = 'ExpenseSource'));

delete from transactionable_examples where transactionable_type = 'ExpenseCategory' or transactionable_type = 'IncomeSource';

insert into transactionable_examples
(name, icon_url, transactionable_type, basket_type, created_at, updated_at, localized_key, country, description_localized_key, prototype_key, create_by_default)
values
('Car', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Auto.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.auto', 'AZ AM BY KZ RU TJ TM UA UZ', 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.auto.sng', 'auto', true),
('Car', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Auto.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.auto', 'AF AX AL DZ AS AD AO AI AQ AG AR AW AU AT BS BH BD BB BE BZ BJ BM BT BO BQ BA BW BV  BR IO BN BG BF BI KH CM CA CV KY CF TD CL CN CX CC CO KM CG CD CK CR CI HR CU CW CY CZ DK DJ DM DO EC EG SV GQ ER EE ET FK FO FJ FI FR GF PF TF GA GM GE DE GH GI GR GL GD GP GU GT GG GN GW GY HT HM VA HN HK HU IS IN ID IR IQ IE IM IL IT JM JP JE JO KE KG KI KP KR KW LA LV LB LS LR LY LI LT LU MD MO MK MG MW MY MV ML MT MH MQ MR MU YT MX FM MC MN ME MS MA MZ MM NA NR NP NL NC NZ NI NE NG NU NF MP NO OM PK PW PS PA PG PY PE PH PN PL PT PR QA RE RO RW BL SH KN LC MF PM VC WS SM ST SA SN RS SC SL SG SX SK SI SB SO ZA GS SS ES LK SD SR SJ SZ SE CH SY TW TZ TH TL TG TK TO TT TN TR TC TV UG AE GB US UM UY VU VE VN VG VI WF EH YE ZM ZW', 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.auto', 'auto', true),
('Transportation', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Bus.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.transportation', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.transportation', 'transportation', true),
('Car rental', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Home_1.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.car_rental', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.car_rental', 'car_rental', false),
('Utilities', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/WiFi.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.utilities', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.utilities', 'utilities', true),
('Phone', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Phone.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.phone', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.phone', 'phone', true),
('Advertising', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Trend.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.advertising', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.advertising', 'advertising', false),
('Office supplies', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Pencil.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.office_supplies', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.office_supplies', 'office_supplies', false),
('Shipping', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Business_2.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.shipping', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.shipping', 'shipping', false),
('Education', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Education.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.education', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.education', 'education', true),
('Entertainment', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Music.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.entertainment', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.entertainment', 'entertainment', true),
('Bank fees', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Machine.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.provider_fee', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.provider_fee', 'provider_fee', true),
('Services', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Repair.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.service_fee', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.service_fee', 'service_fee', false),
('Taxes', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.taxes', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.taxes', 'taxes', true),
('Cafes', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Cafe.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.cafes_and_restaurants', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.cafes_and_restaurants', 'cafes_and_restaurants', true),
('Groceries', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Apple.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.groceries', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.groceries', 'groceries', true),
('Gifts', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Gift.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.gifts_and_donations', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.gifts_and_donations', 'gifts_and_donations', true),
('Health', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Medicine.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.health', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.health', 'health', true),
('Sports', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Ball.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.sports', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.sports', 'sports', true),
('Home', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Home.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.home', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.home', 'home', true),
('Insurance', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/calculator.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.insurance', 'AZ AM BY KZ RU TJ TM UA UZ', 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.insurance.sng', 'insurance', true),
('Insurance', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/calculator.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.insurance', 'AF AX AL DZ AS AD AO AI AQ AG AR AW AU AT BS BH BD BB BE BZ BJ BM BT BO BQ BA BW BV  BR IO BN BG BF BI KH CM CA CV KY CF TD CL CN CX CC CO KM CG CD CK CR CI HR CU CW CY CZ DK DJ DM DO EC EG SV GQ ER EE ET FK FO FJ FI FR GF PF TF GA GM GE DE GH GI GR GL GD GP GU GT GG GN GW GY HT HM VA HN HK HU IS IN ID IR IQ IE IM IL IT JM JP JE JO KE KG KI KP KR KW LA LV LB LS LR LY LI LT LU MD MO MK MG MW MY MV ML MT MH MQ MR MU YT MX FM MC MN ME MS MA MZ MM NA NR NP NL NC NZ NI NE NG NU NF MP NO OM PK PW PS PA PG PY PE PH PN PL PT PR QA RE RO RW BL SH KN LC MF PM VC WS SM ST SA SN RS SC SL SG SX SK SI SB SO ZA GS SS ES LK SD SR SJ SZ SE CH SY TW TZ TH TL TG TK TO TT TN TR TC TV UG AE GB US UM UY VU VE VN VG VI WF EH YE ZM ZW', 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.insurance', 'insurance', true),
('Kids', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Child.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.kids', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.kids', 'kids', true),
('Pets', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Fish.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.pets', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.pets', 'pets', true),
('Shopping', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Shop.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.shopping', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.shopping', 'shopping', true),
('Travel', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Airplane.png', 'ExpenseCategory', 0, now(), now(), 'activerecord.defaults.models.transactionable_example.expense_category.attributes.name.travel', null, 'activerecord.defaults.models.transactionable_example.expense_category.attributes.description.travel', 'travel', true),
('Paycheck', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png', 'IncomeSource', null, now(), now(), 'activerecord.defaults.models.transactionable_example.income_source.attributes.name.paycheck', null, 'activerecord.defaults.models.transactionable_example.income_source.attributes.description.paycheck', 'paycheck', true),
('Bonus', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Gold.png', 'IncomeSource', null, now(), now(), 'activerecord.defaults.models.transactionable_example.income_source.attributes.name.bonus', null, 'activerecord.defaults.models.transactionable_example.income_source.attributes.description.bonus', 'bonus', true),
('Freelance', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/expense_sources/3.png', 'IncomeSource', null, now(), now(), 'activerecord.defaults.models.transactionable_example.income_source.attributes.name.freelance', null, null, 'freelance', true),
('Tuition', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Education.png', 'IncomeSource', null, now(), now(), 'activerecord.defaults.models.transactionable_example.income_source.attributes.name.tuition', null, 'activerecord.defaults.models.transactionable_example.income_source.attributes.description.tuition', 'tuition', true),
('Pension', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/expense_sources/3.png', 'IncomeSource', null, now(), now(), 'activerecord.defaults.models.transactionable_example.income_source.attributes.name.pension', null, null, 'pension', true),
('Other', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/expense_sources/3.png', 'IncomeSource', null, now(), now(), 'activerecord.defaults.models.transactionable_example.income_source.attributes.name.other', null, null, 'other', true),
('Allowance', 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Child.png', 'IncomeSource', null, now(), now(), 'activerecord.defaults.models.transactionable_example.income_source.attributes.name.allowance', null, 'activerecord.defaults.models.transactionable_example.income_source.attributes.description.allowance', 'allowance', false);

update expense_categories set name = 'Cafes', prototype_key = 'cafes_and_restaurants', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Cafe.png', description = 'Cafes, restaurants, pizzerias, canteens, steakhouses, gelaterias, trattorias, alcohol sellers, bars, pubs, nightclubs'
where name = 'Cafe';
update transactions set destination_title = 'Cafes', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Cafe.png'
where destination_title = 'Cafe';

update expense_categories set name = 'Transportation', prototype_key = 'transportation', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Bus.png' , description = 'Public transportation, train, metro, taxi'
where name = 'Bus';
update transactions set destination_title = 'Transportation', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Bus.png'
where destination_title = 'Bus';

update expense_categories set name = 'Services', prototype_key = 'service_fee', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Repair.png' , description = 'Photo studio services, laundry, repairs, restoration'
where name = 'Repair';
update transactions set destination_title = 'Services', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Repair.png'
where destination_title = 'Repair';

update expense_categories set name = 'Shopping', prototype_key = 'shopping', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Shop.png', description = 'Clothing, shoes, jewelry, sunglasses, electronics, software, hardware, computers, equipment that completes the sport, as gym clothing, motorcycle gear'
where name = 'Shop';
update transactions set destination_title = 'Shopping', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Shop.png'
where destination_title = 'Shop';

update expense_categories set name = 'Travel', prototype_key = 'travel', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Airplane.png', description = 'Accommodation, hotels, motels, resorts, air transportation, yachts, holiday companies, specific trips or journies'
where name = 'Airplane';
update transactions set destination_title = 'Travel', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Airplane.png'
where destination_title = 'Airplane';

update expense_categories set name = 'Car', prototype_key = 'auto', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Auto.png', description = 'Fuels, lubricants, parking, automotive parts, accessories, car wash, car repairs, insurance for cars, trucks, motorcycles, other road vehicles'
where name = 'Auto';
update transactions set destination_title = 'Car', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Auto.png'
where destination_title = 'Auto';

update expense_categories set name = 'Kids', prototype_key = 'kids', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Child.png', description = 'Child allowance, babysitting, kids daycare services, baby products, baby food, child support, amusement parks for kids, theme parks for kids, different types of toys'
where name = 'Child';
update transactions set destination_title = 'Kids', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Child.png'
where destination_title = 'Child';

update expense_categories set name = 'Taxes', prototype_key = 'taxes', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png', description = 'Different types of taxes, as income taxes, property taxes'
where name = 'Coins';
update transactions set destination_title = 'Taxes', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png'
where destination_title = 'Coins';

update expense_categories set name = 'Pets', prototype_key = 'pets', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Fish.png', description = 'Products, food for pets, pet grooming services, pet health care including veterinary clinics, pharmacies'
where name = 'Fish';
update transactions set destination_title = 'Pets', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Fish.png'
where destination_title = 'Fish';

update expense_categories set name = 'Gifts', prototype_key = 'gifts_and_donations', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Gift.png', description = 'Greetings cards, gift, souvenir shops, donations, charitable services'
where name = 'Gift';
update transactions set destination_title = 'Gifts', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Gift.png'
where destination_title = 'Gift';

update expense_categories set name = 'Health', prototype_key = 'health', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Heart.png', description = 'Hospitals, dental clinics, medical services, drugstores products, medicaments, personal hygiene, beautification, beauty spas, hair services, nail salons, massage'
where name = 'Heart';
update transactions set destination_title = 'Health', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Heart.png'
where destination_title = 'Heart';

update expense_categories set name = 'Sports', prototype_key = 'sports', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Sport.png', description = 'Sportclubs, gyms, fitness centers, swimming pools'
where name = 'Sport';
update transactions set destination_title = 'Sports', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Sport.png'
where destination_title = 'Sport';

update expense_categories set prototype_key = 'entertainment', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Music.png', description = 'Amusement parks, art dealers, art galleries, video games, gambling, betting, casinos, lotteries, movie streaming services, audio streaming platforms, radio, cinemas, music festivals, newspapers, magazines, their distributors'
where name = 'Entertainment';
update transactions set destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Music.png'
where destination_title = 'Entertainment';

update expense_categories set prototype_key = 'home', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Home.png', description = 'Furniture, interior design services, improvements to outdoor structures, home maintenance, repair, cleaning, laundry, ironing, building materials, household hardware, cookware, domestic appliances, property rentals'
where name = 'Home';
update transactions set destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Home.png'
where destination_title = 'Home';

update expense_categories set prototype_key = 'phone', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Phone.png', description = 'Cell phone, landline phone bills'
where name = 'Phone';
update transactions set destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Phone.png'
where destination_title = 'Phone';

update expense_categories set prototype_key = 'groceries', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Apple.png', description = 'Grocery stores, supermarkets'
where name = 'Groceries';
update transactions set destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Apple.png'
where destination_title = 'Groceries';

update income_sources set name = 'Paycheck', prototype_key = 'paycheck', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png', description = 'Salaries, wages'
where name = 'Job';
update transactions set source_title = 'Paycheck', source_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png'
where source_title = 'Job';

update income_sources set name = 'Tuition', prototype_key = 'tuition', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Education.png', description = 'Tuition grants, scholarships'
where name = 'Scholarship';
update transactions set source_title = 'Tuition', source_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Education.png'
where source_title = 'Scholarship';

update expense_categories set prototype_key = 'cafes_and_restaurants', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Cafe.png', description = 'Кафе, рестораны, пиццерии, столовые, стейк-хаусы, гелатерии, траттории, продавцы алкоголя, бары, пабы, ночные клубы'
where name = 'Кафе';
update transactions set destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Cafe.png'
where destination_title = 'Кафе';

update expense_categories set name = 'Транспорт', prototype_key = 'transportation', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Bus.png' , description = 'Общественный транспорт, поезд, метро, такси'
where name = 'Транспорт';
update transactions set destination_title = 'Транспорт', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Bus.png'
where destination_title = 'Транспорт';

update expense_categories set name = 'Услуги', prototype_key = 'service_fee', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Repair.png' , description = 'Услуги фотостудии, прачечная, ремонт и реставрация'
where name = 'Услуги';
update transactions set destination_title = 'Услуги', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Repair.png'
where destination_title = 'Услуги';

update expense_categories set name = 'Покупки', prototype_key = 'shopping', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Shop.png', description = 'Одежда, обувь, украшения, солнцезащитные очки, электроника, программное обеспечение, оборудование, компьютеры, снаряжение, которое дополняет спорт: спортивная одежда, экипировка для мотоциклов'
where name = 'Покупки';
update transactions set destination_title = 'Покупки', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Shop.png'
where destination_title = 'Покупки';

update expense_categories set name = 'Путешествия', prototype_key = 'travel', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Airplane.png', description = 'Проживание, отели, мотели, курорты, авиаперевозки, яхты, праздничные компании, специальные поездки или путешествия'
where name = 'Путешествия';
update transactions set destination_title = 'Путешествия', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Airplane.png'
where destination_title = 'Путешествия';

update expense_categories set name = 'Автомобиль', prototype_key = 'auto', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Auto.png', description = 'Топливо, смазочные материалы, парковка, автозапчасти, автомойка, ремонт автомобилей, страхование автомобилей, грузовиков, мотоциклов и других дорожных транспортных средств'
where name = 'Автомобиль';
update transactions set destination_title = 'Автомобиль', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Auto.png'
where destination_title = 'Автомобиль';

update expense_categories set name = 'Дети', prototype_key = 'kids', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Child.png', description = 'Пособие на ребенка, услуги няни, детский сад, детские товары, детское питание, поддержка ребенка, парки развлечений для детей, тематические парки для детей, разные виды игрушек'
where name = 'Дети';
update transactions set destination_title = 'Дети', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Child.png'
where destination_title = 'Дети';

update expense_categories set name = 'Налоги', prototype_key = 'taxes', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png', description = 'Различные виды налогов, подоходные налоги, налоги на имущество'
where name = 'Налоги';
update transactions set destination_title = 'Налоги', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png'
where destination_title = 'Налоги';

update expense_categories set name = 'Животные', prototype_key = 'pets', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Fish.png', description = 'Товары и корм для домашних животных, услуги по уходу за животными, уход за животными, включая ветеринарные клиники, аптеки'
where name = 'Животные';
update transactions set destination_title = 'Животные', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Fish.png'
where destination_title = 'Животные';

update expense_categories set name = 'Подарки', prototype_key = 'gifts_and_donations', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Gift.png', description = 'Поздравительные открытки, магазины подарков и сувениров, пожертвования, благотворительные услуги'
where name = 'Подарки';
update transactions set destination_title = 'Подарки', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Gift.png'
where destination_title = 'Подарки';

update expense_categories set name = 'Здоровье', prototype_key = 'health', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Heart.png', description = 'Больницы, стоматологические клиники, медицинские услуги, аптечная продукция, медикаменты, личная гигиена и украшение, салоны красоты, парикмахерские услуги, маникюрные салоны, массажные салоны'
where name = 'Здоровье';
update transactions set destination_title = 'Здоровье', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Heart.png'
where destination_title = 'Здоровье';

update expense_categories set name = 'Спорт', prototype_key = 'sports', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Sport.png', description = 'Спортивные клубы, спортивные залы, фитнес-центры, бассейны'
where name = 'Спорт';
update transactions set destination_title = 'Спорт', destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Sport.png'
where destination_title = 'Спорт';

update expense_categories set prototype_key = 'entertainment', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Music.png', description = 'Парки развлечений, художественные галереи, видеоигры, азартные игры, пари, казино, лотереи, услуги потокового воспроизведения фильмов, платформы потокового аудио, радио, кинотеатры и музыкальные фестивали, газеты, журналы и их распространители'
where name = 'Развлечения';
update transactions set destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Music.png'
where destination_title = 'Развлечения';

update expense_categories set prototype_key = 'home', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Home.png', description = 'Мебель, услуги дизайна интерьера, улучшения наружных конструкций, обслуживание и ремонт дома, уборка, стирка, глажка, строительные материалы, бытовая техника, посуда, бытовая техника, аренда недвижимости'
where name = 'Дом';
update transactions set destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Home.png'
where destination_title = 'Дом';

update expense_categories set prototype_key = 'phone', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Phone.png', description = 'Сотовый телефон, стационарный телефон'
where name = 'Телефон';
update transactions set destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Phone.png'
where destination_title = 'Телефон';

update expense_categories set prototype_key = 'groceries', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Apple.png', description = 'Продуктовые магазины, супермаркеты'
where name = 'Продукты';
update transactions set destination_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Apple.png'
where destination_title = 'Продукты';

update income_sources set name = 'Зарплата', prototype_key = 'paycheck', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png', description = 'Зарплата'
where name = 'Работа';
update transactions set source_title = 'Зарплата', source_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png'
where source_title = 'Работа';

update income_sources set name = 'Учеба', prototype_key = 'tuition', icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Education.png', description = 'Учебные гранты и стипендии'
where name = 'Стипендия';
update transactions set source_title = 'Учеба', source_icon_url = 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Education.png'
where source_title = 'Стипендия';

update transactionable_examples t
set prototype_key = substr(t.localized_key, 85)
where transactionable_type = 'ExpenseSource';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = e.name and te.transactionable_type = 'ExpenseSource' limit 1);

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Cash' limit 1)
where e.name = 'Наличные';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Sberbank' limit 1)
where e.name = 'Сбербанк';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Tinkoff' limit 1)
where e.name = 'Тинькофф банк';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Alpha Bank' limit 1)
where e.name = 'Альфа-банк';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Gazprom' limit 1)
where e.name = 'Газпромбанк';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'VTB' limit 1)
where e.name = 'ВТБ';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'RocketBank' limit 1)
where e.name = 'Рокетбанк';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Rosbank' limit 1)
where e.name = 'Росбанк';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Bank Otkrytie' limit 1)
where e.name = 'Открытие';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Rosselhoz Bank' limit 1)
where e.name = 'Россельхозбанк';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Moscow Credit' limit 1)
where e.name = 'Московский кредитный банк';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Unicredit Bank' limit 1)
where e.name = 'Юникредит банк';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Raiffaizen' limit 1)
where e.name = 'Райффайзенбанк';

update expense_sources e set prototype_key = (select te.prototype_key from transactionable_examples te
                                              where te.name = 'Sovkombank' limit 1)
where e.name = 'Совкомбанк';

