FROM php:8.4-apache

RUN docker-php-ext-install pdo_mysql mysqli \
    && a2enmod rewrite

COPY php.ini /usr/local/etc/php/conf.d/tienda.ini
COPY index.php /var/www/html/index.php
