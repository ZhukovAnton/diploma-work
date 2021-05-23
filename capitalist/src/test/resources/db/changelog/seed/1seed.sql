INSERT INTO public.active_types (id, name, localized_key, created_at, updated_at, row_order, deleted_at, default_planned_income_type, is_goal_amount_required, is_income_planned_default, only_buying_assets, cost_title_localized_key, monthly_payment_localized_key, buying_assets_default, buying_assets_title_localized_key) VALUES (1, 'Stocks', 'activerecord.defaults.models.active_type.attributes.name.stocks', '2019-10-27 18:52:41.610908', '2019-11-18 23:56:53.153194', 0, null, 0, false, true, false, 'activerecord.defaults.models.active_type.attributes.cost_title.stocks', 'activerecord.defaults.models.active_type.attributes.monthly_payment_title.stocks', true, 'activerecord.defaults.models.active_type.attributes.buying_assets_title.stocks');
INSERT INTO public.active_types (id, name, localized_key, created_at, updated_at, row_order, deleted_at, default_planned_income_type, is_goal_amount_required, is_income_planned_default, only_buying_assets, cost_title_localized_key, monthly_payment_localized_key, buying_assets_default, buying_assets_title_localized_key) VALUES (2, 'Real Estate', 'activerecord.defaults.models.active_type.attributes.name.real_estate', '2019-10-27 18:52:41.639654', '2019-11-18 23:56:53.159258', 1073741824, null, 1, false, true, false, 'activerecord.defaults.models.active_type.attributes.cost_title.real_estate', 'activerecord.defaults.models.active_type.attributes.monthly_payment_title.real_estate', false, 'activerecord.defaults.models.active_type.attributes.buying_assets_title.real_estate');
INSERT INTO public.active_types (id, name, localized_key, created_at, updated_at, row_order, deleted_at, default_planned_income_type, is_goal_amount_required, is_income_planned_default, only_buying_assets, cost_title_localized_key, monthly_payment_localized_key, buying_assets_default, buying_assets_title_localized_key) VALUES (3, 'Goal', 'activerecord.defaults.models.active_type.attributes.name.goal', '2019-10-27 18:52:41.651228', '2019-11-18 23:56:53.164661', 1610612736, null, 1, true, false, true, 'activerecord.defaults.models.active_type.attributes.cost_title.goal', 'activerecord.defaults.models.active_type.attributes.monthly_payment_title.goal', true, 'activerecord.defaults.models.active_type.attributes.buying_assets_title.goal');
INSERT INTO public.active_types (id, name, localized_key, created_at, updated_at, row_order, deleted_at, default_planned_income_type, is_goal_amount_required, is_income_planned_default, only_buying_assets, cost_title_localized_key, monthly_payment_localized_key, buying_assets_default, buying_assets_title_localized_key) VALUES (4, 'Other', 'activerecord.defaults.models.active_type.attributes.name.other', '2019-10-27 18:52:41.664158', '2019-11-18 23:56:53.170467', 1879048192, null, 1, false, true, false, 'activerecord.defaults.models.active_type.attributes.cost_title.other', 'activerecord.defaults.models.active_type.attributes.monthly_payment_title.other', false, 'activerecord.defaults.models.active_type.attributes.buying_assets_title.other');

INSERT INTO public.credit_types (id, name, localized_key, period_unit, min_value, max_value, default_value, has_monthly_payments, period_super_unit, units_in_super_unit, row_order, deleted_at, created_at, updated_at, is_default) VALUES (1, 'Microcredit', 'activerecord.defaults.models.credit_type.attributes.name.мicrofinance', 0, 1, 60, 5, false, null, null, 0, null, '2019-09-30 05:21:06.955980', '2019-09-30 05:21:06.955980', false);
INSERT INTO public.credit_types (id, name, localized_key, period_unit, min_value, max_value, default_value, has_monthly_payments, period_super_unit, units_in_super_unit, row_order, deleted_at, created_at, updated_at, is_default) VALUES (3, 'Mortgage', 'activerecord.defaults.models.credit_type.attributes.name.mortgage', 2, 5, 30, 10, true, null, null, 1610612736, null, '2019-09-30 05:21:06.979822', '2019-09-30 05:21:06.979822', false);
INSERT INTO public.credit_types (id, name, localized_key, period_unit, min_value, max_value, default_value, has_monthly_payments, period_super_unit, units_in_super_unit, row_order, deleted_at, created_at, updated_at, is_default) VALUES (2, 'Credit', 'activerecord.defaults.models.credit_type.attributes.name.credit', 1, 2, 60, 12, true, 2, 12, 1073741824, null, '2019-09-30 05:21:06.967421', '2019-11-15 19:54:53.446848', true);

INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1893, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/expense_sources/1.png', 0, '2020-05-09 15:24:29.191754', '2020-05-09 15:24:29.191754');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1894, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/expense_sources/2.png', 0, '2020-05-09 15:24:29.199393', '2020-05-09 15:24:29.199393');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1895, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/expense_sources/3.png', 0, '2020-05-09 15:24:29.206479', '2020-05-09 15:24:29.206479');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1896, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/expense_sources/4.png', 0, '2020-05-09 15:24:29.213197', '2020-05-09 15:24:29.213197');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1897, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/expense_sources/5.png', 0, '2020-05-09 15:24:29.220387', '2020-05-09 15:24:29.220387');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1898, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/expense_sources/6.png', 0, '2020-05-09 15:24:29.227929', '2020-05-09 15:24:29.227929');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1899, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/calculator.png', 1, '2020-05-09 15:24:29.239895', '2020-05-09 15:24:29.239895');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1900, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Home.png', 1, '2020-05-09 15:24:29.248133', '2020-05-09 15:24:29.248133');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1901, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Cafe.png', 1, '2020-05-09 15:24:29.255073', '2020-05-09 15:24:29.255073');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1902, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Shop.png', 1, '2020-05-09 15:24:29.261819', '2020-05-09 15:24:29.261819');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1903, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Music.png', 1, '2020-05-09 15:24:29.268763', '2020-05-09 15:24:29.268763');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1904, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Medicine.png', 1, '2020-05-09 15:24:29.275708', '2020-05-09 15:24:29.275708');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1905, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Sport.png', 1, '2020-05-09 15:24:29.282655', '2020-05-09 15:24:29.282655');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1906, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Child.png', 1, '2020-05-09 15:24:29.290034', '2020-05-09 15:24:29.290034');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1907, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Top.png', 1, '2020-05-09 15:24:29.297921', '2020-05-09 15:24:29.297921');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1908, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Goal.png', 1, '2020-05-09 15:24:29.305404', '2020-05-09 15:24:29.305404');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1909, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Tv.png', 1, '2020-05-09 15:24:29.312144', '2020-05-09 15:24:29.312144');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1910, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Groceries.png', 1, '2020-05-09 15:24:29.318942', '2020-05-09 15:24:29.318942');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1911, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Education.png', 1, '2020-05-09 15:24:29.326216', '2020-05-09 15:24:29.326216');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1912, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Business.png', 1, '2020-05-09 15:24:29.333389', '2020-05-09 15:24:29.333389');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1913, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Taxi.png', 1, '2020-05-09 15:24:29.339832', '2020-05-09 15:24:29.339832');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1914, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Taxi_1.png', 1, '2020-05-09 15:24:29.346594', '2020-05-09 15:24:29.346594');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1915, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Bus.png', 1, '2020-05-09 15:24:29.353074', '2020-05-09 15:24:29.353074');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1916, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Bus_1.png', 1, '2020-05-09 15:24:29.359741', '2020-05-09 15:24:29.359741');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1917, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Bus_2.png', 1, '2020-05-09 15:24:29.366677', '2020-05-09 15:24:29.366677');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1918, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Auto.png', 1, '2020-05-09 15:24:29.373489', '2020-05-09 15:24:29.373489');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1919, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Repair.png', 1, '2020-05-09 15:24:29.380084', '2020-05-09 15:24:29.380084');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1920, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Business_2.png', 1, '2020-05-09 15:24:29.387214', '2020-05-09 15:24:29.387214');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1921, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Subpole.png', 1, '2020-05-09 15:24:29.393782', '2020-05-09 15:24:29.393782');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1922, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/WiFi.png', 1, '2020-05-09 15:24:29.400311', '2020-05-09 15:24:29.400311');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1923, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Book.png', 1, '2020-05-09 15:24:29.407113', '2020-05-09 15:24:29.407113');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1924, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Suitcase.png', 1, '2020-05-09 15:24:29.413641', '2020-05-09 15:24:29.413641');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1925, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Security.png', 1, '2020-05-09 15:24:29.420602', '2020-05-09 15:24:29.420602');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1926, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Machine.png', 1, '2020-05-09 15:24:29.427538', '2020-05-09 15:24:29.427538');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1927, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Airplane.png', 1, '2020-05-09 15:24:29.433929', '2020-05-09 15:24:29.433929');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1928, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Clothes.png', 1, '2020-05-09 15:24:29.440815', '2020-05-09 15:24:29.440815');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1929, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Heart.png', 1, '2020-05-09 15:24:29.447115', '2020-05-09 15:24:29.447115');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1930, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Gift.png', 1, '2020-05-09 15:24:29.454374', '2020-05-09 15:24:29.454374');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1931, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Home_1.png', 1, '2020-05-09 15:24:29.461088', '2020-05-09 15:24:29.461088');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1932, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Fastfood.png', 1, '2020-05-09 15:24:29.467648', '2020-05-09 15:24:29.467648');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1933, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Entertainment.png', 1, '2020-05-09 15:24:29.475536', '2020-05-09 15:24:29.475536');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1934, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Phone.png', 1, '2020-05-09 15:24:29.484327', '2020-05-09 15:24:29.484327');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1935, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Coins.png', 1, '2020-05-09 15:24:29.491148', '2020-05-09 15:24:29.491148');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1936, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Fish.png', 1, '2020-05-09 15:24:29.497956', '2020-05-09 15:24:29.497956');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1937, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Paper.png', 1, '2020-05-09 15:24:29.504583', '2020-05-09 15:24:29.504583');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1938, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Ball.png', 1, '2020-05-09 15:24:29.512736', '2020-05-09 15:24:29.512736');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1939, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Target.png', 1, '2020-05-09 15:24:29.520886', '2020-05-09 15:24:29.520886');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1940, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Tennis.png', 1, '2020-05-09 15:24:29.527389', '2020-05-09 15:24:29.527389');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1941, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Field.png', 1, '2020-05-09 15:24:29.534124', '2020-05-09 15:24:29.534124');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1942, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Bicycle.png', 1, '2020-05-09 15:24:29.540815', '2020-05-09 15:24:29.540815');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1943, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Board.png', 1, '2020-05-09 15:24:29.547640', '2020-05-09 15:24:29.547640');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1944, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Pencil.png', 1, '2020-05-09 15:24:29.554381', '2020-05-09 15:24:29.554381');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1945, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Book1.png', 1, '2020-05-09 15:24:29.560965', '2020-05-09 15:24:29.560965');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1946, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Glases.png', 1, '2020-05-09 15:24:29.567669', '2020-05-09 15:24:29.567669');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1947, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Apple.png', 1, '2020-05-09 15:24:29.573919', '2020-05-09 15:24:29.573919');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1948, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Fork.png', 1, '2020-05-09 15:24:29.580344', '2020-05-09 15:24:29.580344');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1949, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Bottle.png', 1, '2020-05-09 15:24:29.587100', '2020-05-09 15:24:29.587100');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1950, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Sausage.png', 1, '2020-05-09 15:24:29.593618', '2020-05-09 15:24:29.593618');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1951, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Convertation.png', 1, '2020-05-09 15:24:29.600251', '2020-05-09 15:24:29.600251');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1952, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Rouble.png', 1, '2020-05-09 15:24:29.606809', '2020-05-09 15:24:29.606809');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1953, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Trend.png', 1, '2020-05-09 15:24:29.613327', '2020-05-09 15:24:29.613327');
INSERT INTO public.icons (id, url, category, created_at, updated_at) VALUES (1954, 'https://s3.eu-central-1.amazonaws.com/skrudzh/v3/common/Gold.png', 1, '2020-05-09 15:24:29.619782', '2020-05-09 15:24:29.619782');