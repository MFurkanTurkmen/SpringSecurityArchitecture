# Spring Security ve JWT Kimlik Doğrulama

Spring Security ve JWT (JSON Web Tokens) kullanarak güvenlik ve method bazlı kimlik doğrulama yapan bir Spring Boot uygulaması.

## Özellikler

- JWT kimlik doğrulaması ile kullanıcı kaydı ve girişi
- BCrypt kullanarak şifre şifreleme
- Spring Security ile rol tabanlı yetkilendirme
- Özelleştirilmiş erişim reddedildi işleme
- Swagger UI entegrasyonu
- PostgreSQL veritabanı
- İstisna yönetimi

## Teknolojiler

- Java 21
- Spring Boot 3.3.5
- Spring Security
- JSON Web Tokens (JWT)
- BCrypt
- PostgreSQL
- Gradle
- Swagger UI
- MapStruct
- Lombok

## Başlangıç

### Gereksinimler

- JDK 21
- Gradle
- PostgreSQL

### Veritabanı Yapılandırması

Bir PostgreSQL veritabanı oluşturun ve application.yml dosyasını veritabanı yapılandırmanızla güncelleyin:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5444/springsecurity
    username: "your-username"
    password: "your-password"
```

### Uygulamayı Çalıştırma

1. Depoyu klonlayın
```bash
git clone https://github.com/MFurkanTurkmen/springsecurity.git
```

2. Proje dizinine gidin
```bash
cd springsecurity
```

3. Projeyi derleyin
```bash
./gradlew build
```

4. Uygulamayı çalıştırın
```bash
./gradlew bootRun
```

Uygulama http://localhost:80 adresinde çalışmaya başlayacak

## API Dokümantasyonu

Uygulamayı çalıştırdıktan sonra, Swagger UI'ya şu adresten erişebilirsiniz:
```
http://localhost/swagger-ui/index.html
```

## API Uç Noktaları

### Kimlik Doğrulama Denetleyicisi

| Metod | Url | Açıklama |
| ---- | --- | ---- |
| POST   | /auth/signup | Yeni kullanıcı kaydı |
| POST   | /auth/login  | Kullanıcı girişi |

### Kitap Denetleyicisi

| Metod | Url | Açıklama | Gerekli Rol |
| ---- | --- | ---- | ---- |
| GET    | /books | Tüm kitapları getir | ROLE_USER |
| GET    | /books/{id} | ID'ye göre kitap getir | ROLE_USER |
| POST   | /books | Yeni kitap oluştur | ROLE_ADMIN |

## Güvenlik

Uygulama, kimlik doğrulama için JWT (JSON Web Tokens) kullanır. JWT gizli anahtarı application.yml dosyasında yapılandırılır:

```yaml
jwt:
  secret: "your-secret-key"
```

## Rol Tabanlı Erişim Kontrolü

Uygulama aşağıdaki rolleri destekler:
- ROLE_USER
- ROLE_MODERATOR
- ROLE_ADMIN

## Hata Yönetimi

Uygulama, aşağıdakileri işleyen global bir istisna işleyicisi içerir:
- Kimlik doğrulama istisnaları
- Doğrulama istisnaları
- Genel uygulama istisnaları

## Yazar

Muhammed Furkan Türkmen
- GitHub: [@MFurkanTurkmen](https://github.com/MFurkanTurkmen)