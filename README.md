# Yugastore in Java

![Homepage](docs/home.png)

## 概要

This is an implementation of a sample ecommerce app. This microservices-based retail marketplace or eCommerce app is composed of **microservices written in Spring (Java)**, a **UI based on React** and **YugabyteDB as the [distributed SQL](https://www.yugabyte.com/tech/distributed-sql/) database**.

If you're using this demo app, please :star: this repository! We appreciate your support.

## 目次

- [概要](#概要)
- [ドキュメント構成](#ドキュメント構成)
- [システム全体像（アーキテクチャ）](#システム全体像アーキテクチャ)
- [ディレクトリ構造（標準）](#ディレクトリ構造標準)
- [共通設計原則](#共通設計原則)
- [技術スタック](#技術スタック)
- [クイックスタート](#クイックスタート)

## ドキュメント構成

このリポジトリに含まれるドキュメント構成を以下に示します。

- **README.md**（このファイル）- プロジェクト全体のガイド（読了時間: 15分）
  - システム全体のアーキテクチャと概要
  - 技術スタックとセットアップ手順
  - クイックスタートガイド
- **resources/README.md** - データローディング手順（読了時間: 5分）
  - メタデータのパース方法
  - YugabyteDBへのデータロード手順
  - データクエリの例
- **api-gateway-microservice/README.md** - API Gatewayサービスのデプロイ手順（読了時間: 3分）
- **products-microservice/README.md** - Productsサービスのデプロイ手順（読了時間: 3分）
- **react-ui/README.md** - React UIとSpring Bootの統合チュートリアル（読了時間: 20分）

## システム全体像（アーキテクチャ）

The architecture diagram of Yugastore is shown below.

![Architecture of microservices based retail marketplace app](yugastore-java-architecture.png)


| Microservice         | YugabyteDB API | Default host:port | Description           |
| -------------------- | ---------------- | ---------------- | --------------------- |
| [service discovery](https://github.com/yugabyte/yugastore-java/tree/master/eureka-server-local) | - | [localhost:8761](http://localhost:8761) | Uses **Eureka** for localhost. All microservices register with the Eureka service. This registration information is used to discover dynamic properties of any microservice. Examples of discovery include finding the hostnames or ip addresses, the load balancer and the port on which the microservice is currently running.
| [react-ui](https://github.com/yugabyte/yugastore-java/tree/master/react-ui) | - | [localhost:8080](http://localhost:8080) | A react-based UI for the eCommerce online marketplace app.
| [api-gateway](https://github.com/yugabyte/yugastore-java/tree/master/api-gateway-microservice) | - | [localhost:8081](http://localhost:8081) | This microservice handles all the external API requests. The UI only communicates with this microservice.
| [products](https://github.com/yugabyte/yugastore-java/tree/master/products-microservice) | YCQL | [localhost:8082](http://localhost:8082) | This microservice contains the entire product catalog. It can list products by categories, return the most popular products as measured by sales rank, etc.
| [cart](https://github.com/yugabyte/yugastore-java/tree/master/cart-microservice) | YSQL | [localhost:8083](http://localhost:8083) | This microservice deals with users adding items to the shopping cart. It has to be necessarily highly available, low latency and often multi-region.
| [checkout](https://github.com/yugabyte/yugastore-java/tree/master/checkout-microservice) | YCQL | [localhost:8086](http://localhost:8086) | This deals with the checkout process and the placed order. It also manages the inventory of all the products because it needs to ensure the product the user is about to order is still in stock.
| [login](https://github.com/yugabyte/yugastore-java/tree/master/login-microservice) | YSQL | [localhost:8085](http://localhost:8085) | Handles login and authentication of the users. *Note that this is still a work in progress.*

## ディレクトリ構造（標準）

```
yugastore-java-ef/
├── README.md                      # このファイル
├── pom.xml                        # 親Maven設定（全マイクロサービスをモジュールとして管理）
├── docker-run.sh                  # Docker環境起動スクリプト
├── yugastore-java-architecture.png # アーキテクチャ図
├── api-gateway-microservice/      # API Gatewayサービス（ポート8081）
│   ├── src/main/java/             # Javaソースコード
│   ├── pom.xml                    # Maven設定
│   └── Dockerfile                 # Docker設定
├── cart-microservice/             # カートサービス（ポート8083、YSQL）
│   ├── src/main/java/
│   ├── pom.xml
│   └── Dockerfile
├── checkout-microservice/         # チェックアウトサービス（ポート8086、YCQL）
│   ├── src/main/java/
│   ├── pom.xml
│   └── Dockerfile
├── eureka-server-local/           # サービスディスカバリ（ポート8761）
│   ├── src/main/java/
│   ├── pom.xml
│   └── Dockerfile
├── login-microservice/            # ログインサービス（ポート8085、YSQL）
│   ├── src/main/java/
│   └── pom.xml
├── products-microservice/         # 商品カタログサービス（ポート8082、YCQL）
│   ├── src/main/java/
│   ├── pom.xml
│   └── Dockerfile
├── react-ui/                      # Reactフロントエンド（ポート8080）
│   ├── frontend/                  # Reactアプリケーション
│   ├── src/main/java/
│   └── pom.xml
├── resources/                     # データベーススキーマとデータロードスクリプト
│   ├── schema.cql                 # YCQLテーブル定義
│   ├── schema.sql                 # YSQLテーブル定義
│   ├── products.json              # サンプル商品データ
│   └── dataload.sh                # データロードスクリプト
└── docs/                          # スクリーンショット
```

## 共通設計原則

### レイヤードアーキテクチャ

このプロジェクトでは、保守性と拡張性を高めるため、レイヤードアーキテクチャを採用しています。

1. **プレゼンテーション層（Controller層）**: REST APIエンドポイントを公開し、HTTPリクエストを処理
2. **ビジネスロジック層（Service層）**: ビジネスロジックの実装
3. **データアクセス層（Repository層）**: データベースとのやり取りを担当
4. **ドメイン層**: ビジネスエンティティとドメインモデルを定義

### 依存関係のルール

- 上位層は下位層に依存できますが、下位層は上位層に依存してはいけません
- Domain層は他の層に依存してはいけません（純粋なビジネスロジックのみ）

### マイクロサービス間通信

- 各マイクロサービスはEurekaサービスディスカバリに登録
- API GatewayがOpenFeignを使用して他のサービスと通信
- サービス間はREST APIで通信

## 技術スタック

### バージョン情報

* Java 17
* Spring Boot 2.6.3
* Spring Cloud 2021.0.0
* Yugabyte Java Driver 4.6.0-yb-10
* Python 3 (Data Loading)

### 主な特徴

* Written fully in Spring Framework
* Desgined for multi-region and Kubernetes-native deployments
* Features 6 Spring Boot microservices
* Uses a discovery service that the microservices register with
* Sample data has over 6K products in the store

## クイックスタート

To build, simply run the following from the base directory:

```
$ mvn -DskipTests package
```

To run the app on host machine, you need to first install YugabyteDB, create the necessary tables, start each of the microservices and finally the React UI.

## Running the app on host

Make sure you have built the app as described above. Now do the following steps.

## Step 1: Install and initialize YugabyteDB

You can [install YugabyteDB by following these instructions](https://docs.yugabyte.com/latest/quick-start/).

Now create the necessary tables as shown below. Note that these steps would take a few seconds.

```
$ cd resources
$ cqlsh -f schema.cql
```
Next, load some sample data.

```
$ cd resources
$ ./dataload.sh
```

Create the postgres tables in `resources/schema.sql` for the YSQL tables.

## Step 2: Start the Eureka service discovery (local)

You can do this as follows:

```
$ cd eureka-server-local/
$ mvn spring-boot:run
```

Verify this is running by browsing to the [Spring Eureka Service Discovery dashboard](http://localhost:8761/).

## Step 2: Start the api gateway microservice

To run the products microservice, do the following in a separate shell:

```
$ cd api-gateway-microservice/
$ mvn spring-boot:run
```


## Step 3: Start the products microservice

To run the products microservice, do the following in a separate shell:

```
$ cd products-microservice/
$ mvn spring-boot:run
```

## Step 4: Start the checkout microservice

To run the products microservice, do the following in a separate shell:

```
$ cd checkout-microservice/
$ mvn spring-boot:run
```

## Step 5: Start the checkout microservice

To run the cart microservice, do the following in a separate shell:

```
$ cd cart-microservice/
$ mvn spring-boot:run
```

## Step 6: Start the UI

To do this, simply run `npm start` from the `frontend` directory in a separate shell:

```
$ cd react-ui
$ mvn spring-boot:run
```

Now browse to the marketplace app at [http://localhost:8080/](http://localhost:8080/).

# Running the app in docker containers

The dockers images are built along with the binaries when `mvn -DskipTests package` was run.
To run the docker containers, run the following script, after you have [Installed and initialized YugabyteDB](#step-1-install-and-initialize-yugabyte-db):

```
$ ./docker-run.sh
```
Check all the services are registered on the [eureka-server](http://127.0.0.1:8761/).
Once all services are registered, you can browse the marketplace app at [http://localhost:8080/](http://localhost:8080/).



## Screenshots


### Home
![Home Page](docs/home.png)

### Product Category Page

![Product Category](docs/product-category.png)

### Product Detail Page

![Product Page](docs/product.png)

### Car

![Cart](docs/cart.png)

## Checkout

![Checkout](docs/checkout.png)
