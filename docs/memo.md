## 3-1

- HelloController.javaというコントローラを作成しているけど、このコントローラを参照させるための設定はしていない。  
  @Controllerのおかげなのか@GetMappingのおかげなのかよくわからない。
- return文にredirectをつけないと、URLは直前のURLを引き継いでしまう。
- redirectの後に指定するのは、redirect先のControllerの＠GetMappingなどで指定した値。  
- なので、redirect使わないならControllerは不要、使うならControllerが必要。 

## 3-2

### *HTMLのform => Controllerの値の受け渡し*

- HTML: th:value属性を使うことで、画面からControllerクラスに値を渡すことができる。POST先をformタグのaction要素に指定。
- Controller: @PostMappingアノテーションをつけたメソッドの引数に@RequestParamを付けることで、HTMLからの入力内容を受け取れる。  
  アノテーションの引数には（多分th:value属性を指定した箇所の）name属性を指定する。
- formタグのaction要素と同じ値を@PostMappingの引数に指定してメソッドを作成する。

### *Controller => HTMLの画面の値の受け渡し*

- Controller: model.addAttributeに任意のキー、HTMLの入力内容を指定した値をセットする。
- HTML: th:text属性にmodel.addAttributeで登録したキーを指定することで、Controllerから値を受け取れる。

## 3-3

- 一連の処理フロー
  + HTML
  + コントローラクラス: どのサービスを使うかを指定して、サービスの結果を画面に返却
  + サービスクラス: リポジトリクラスを利用
  + （ドメインクラス）: リポジトリクラスやサービスクラスなどの間で渡すクラス
  + リポジトリクラス: DBへのCRUD操作
  + DB

### リポジトリクラス

- JdbcTemplate: Springが用意しているJDBC接続用のクラス。@Autowiredつける。
- queryForMap: jdbcTemplateのメソッド。検索結果をMapに入れる。  
  受け取り側としては、Mapのgetメソッドにテーブルのフィールド名を指定することで、値を取得する。
- @Autowiredは、インスタンスをnewするイメージ

## 4-1

### ざっくり説明

- DIはインスタンス管理をする。@Autowiredアノテーションをフィールドなどに付けると、DIコンテナからインスタンスを取得する。
- 具体的にはインスタンスの生成とインスタンスのライフサイクル管理（破棄）をする。このおかげでクラスをnewしたり、使い終わった変数にnullを入れる必要がなくなる。

## 4-2

- DIは依存性の注入
- 「依存性」と「注入」を分けて考えるとよい

### 4-2-1 インタフェース（依存性の話）

- Carクラスが依存するのをHondaEngineクラスではなく、Engineインタフェースにしておくと、NissanEngineへの切り替え時に、Carクラスの変更が不要になる。
- Carクラスのテストも、DummyEngineクラスを用意してテストすればよい。疎結合。
- ただこれだとMainクラスなど、Carクラスをインスタンス化するところで、HondaEngineやNissanEngineの指定が必要になる。依存性が残る。

### 4-2-2 Factoryメソッドパターン（注入の話）

```java
    Engine hondaEngine1 = new HondaEngine();

    Car car1 = new Car(HondaEngine1);
```

- 上記のように変数にインスタンスを入れることを注入という。
- newするコードがMainクラスにあると修正範囲が大きくなる。
- Factoryメソッドパターンで解決できる。
- 下記のような簡易的なFactoryメソッドパターンであれば、メソッドをstaticにする。こうすることで、Factoryインスタンスを生成せずにメソッドを呼び出せる。  

```java
// Factoryクラス
public class EngineFactory {
    public static Engine createHondaEngine() {
        return new HondaEngineVer2();
    }
}
```

- Mainクラスの修正は不要になる。

```java
// Mainクラス
public void main {
    public static void main(String[] args) {
        Engine hondaEngine1 = EngineFactory.createHondaEngine();
    }
}
```

## 4-3 DI...依存性の注入

### 4-3-1 DIの中の処理

1. DIの管理対象クラスを探す（コンポーネントスキャン）

- **Springを起動すると、コンポーネントスキャンという処理が走り、DI管理対象アノテーションが付いているクラス(=Bean)を探す。**
- 対象アノテーションは以下の通りで、太字がよく使うもの。なおBeanは、正しくは「DIコンテナ上で管理するクラス」のこと。DI管理対象アノテーションをつける以外にもDIコンテナに登録することはできるので、そこは注意。
  + **@Component**
  + **@Controller**
  + **@Service**
  + **@Repository**
  + @Configuration
  + @RestController
  + @ControllAdvice
  + @ManagedBean
  + @Named

2. インスタンスの生成と注入

- **DIコンテナに登録されたBeanのインスタンス生成と注入をする（イメージ）。**
  + 1. でBeanを集めた後は、それらのインスタンスを生成（new）する
  + 生成したインスタンスを@Autowiredアノテーションが付いているフィールドなどに注入する
- DIコンテナがやっていることのイメージとしては、各クラスのインスタンスを生成しておいて、そのインスタンスをgetterで取得できるようにするイメージ。**@Autowiredが付いているフィールドなどでは、DIコンテナのgetterを呼び出しているイメージ。**
- これはFactoryメソッドを使ってインスタンスを取得しているようなイメージ。**つまり@Componentなどのアノテーションを付けるだけで、いちいちFactoryメソッドを作る手間が省ける。**

### 4-3-2 DIの実装方法

- DIの実装方法にはいくつかあって、以下の通り特徴がある
  + アノテーションベース
    - 今までの説明内容
    - 小規模なアプリならこれで作るのがよい
  + JavaConfig
    - 細かい設定や切り替えができるという利点があるが、開発規模が大きくなればなるほど、JavaConfigの中に定義しないといけないメソッドが増えていく。
  + JavaConfig + アノテーションベース
    - JavaConfigとアノテーションベースのハイブリッド
    - DIで管理したいクラスには@Controllerなどのアノテーションを付ける
    - 本番環境と開発環境用を切り替えたい、細かい設定をしたいといったインスタンスのみJavaConfigで設定する。

## 4-4 DIのライフサイクル管理機能

### 4-4-1 DIのライフサイクル管理

- インスタンスの生成は通常newを使って生成する。一方、インスタンスの破棄は変数にnullを入れる
- Springはこのnullを入れるところを自動でやってくれる
- ライフサイクル管理は@Scopeで行う

### 4-4-2 DIの落とし穴その１...singleton

- デフォルトのsingletonでWebアプリを作ると、コントローラ、サービス、リポジトリクラスのインスタンスが１つなので、リクエストを処理しきれなくなる可能性がある

### 4-4-3 DIの落とし穴その２...スコープの違い

- prototypeスコープを持ったコンポーネントをsingletonスコープを持ったコンポーネントの中で生成すると、スコープはsingletonスコープになる

### 6-1-3 画面の作成

- *LoginController.javaにPOSTリクエストがいくケースってどんなケースだろう？*
- *login.htmlのsigninへのリンクは、Thymeleafの記法使わなくても今のところ問題なさそう。この後Springセキュリティ使うから今のうちにThymeleafにしているんだろう。*

```html
    <!--<a th:href="@{'/signup'}">ユーザー新規登録はこちら</a>-->
    <a href="signup">ユーザー新規登録はこちら</a>
```

#### formタグ内のactionの書き方

- Springセキュリティを使わない場合：action="/login"で問題ない
- Springセキュリティを使う場合：th:action="@{/login}"を使う

### 6-2-1 データバインドの概要

- 画面の入力項目とオブジェクトのフィールドのマッピングを行うこと。  
- また、画面から渡された値を、フィールドのデータ型に合わせて変換すること。

### 6-2-2 データバインドの実装（*フォームクラスの説明*）

- HTMLとControllerの間でデータのやり取りをするインスタンスというイメージで、これをフォームクラスと呼ぶ。

#### データバインド用アノテーション一覧

- @NumberFormat: 指定されたフォーマットの文字列を数値型に変換する
- @DateTimeFormat: 指定されたフォーマットの文字列を日付型に変換する

#### 手順

- controllerと同階層にdomain/modelなどのディレクトリ配下にフォームクラスを作成

### フォームクラスの受け渡し

- Contollerのメソッドはフォームクラスの受け渡し設定前後で、以下のように変わる

```java

    // 前
    @PostMapping("/signup")
    public String postSignUp(Model model) {

    // 後
    @PostMapping("/signup")
    public String postSignUp(@ModelAttribute SignupForm form, BindingResult bindingResult, Model model) {
```

- @ModelAttributeアノテーションを付けると、自動でModelクラスに登録してくれる。  
  登録名はデフォルトではクラス名の先頭を小文字に変えた文字列。上だとsignupForm。イメージ的には以下をやってくれる。

```java
    // @ModelAttributeアノテーションがやってくれることのイメージ
    model.addAttribute("loginForm", form);

    //なお、HelloControllerで@RequestParamを使っていた時には、実際にaddAttributeしていた
    @PostMapping("/hello")
    public String postRequest(@RequestParam("text1") String str, Model model) {

        // 画面から受け取った文字列をModelに登録
        model.addAttribute("sample", str);

        // helloResponse.htmlに画面遷移
        return "helloResponse";
    }
```

- BindingResultをメソッドの引数に追加することで、**データバインドの結果を受け取る**。  
  受け取りなので、GET用のメソッドには使わず、POST用のメソッドに使っている。

#### th:object

- th:object属性を使うことで、Modelに登録されているオブジェクトを受け取ることができる。

```
th:object="$<ModelAttributeのキー名>"
```

- th:objectを付けたタグの中であれば、th:field名を省略して取得できる。同時にコントローラクラスに値を渡すこともできる。

```
th:field="$<フィールド名>"
```

### 7.1.2 AOPの用語

- Advice: AOPで実行する処理
- Pointcut: 処理を実行するクラスやメソッド
- JoinPoint: 処理の実行タイミング

#### JoinPoint

||呼び出し|正常終了|異常終了|
|---|:---:|:---:|:---:|
|Before|○|-|-|
|After|-|○|○|
|AfterReturning|-|○|-|
|AfterThrowing|-|-|○|
|Around|○|○|○|

### 7.1.3 AOPの内部の仕組み

- Beanメソッドの呼び出しはProxy経由で行われる。このProxyがAdviceを実行する。
- *AOPされる側のBeanメソッドにはいっさい手が入らない。*

## 8.1 SpringJDBCとは

- JDBC: JavaでDBにアクセスするためのライブラリ
- SpringJDBC: JDBCをSpringでもっと簡単に使えるようにしたもの。  
  イメージ: アプリ <=> SpringJDBC <=> JDBC <=> DB  
  主な利点は以下の通り
  + DBの接続やクローズの処理を書かないで済む
  + DB製品を抽象化して、DB製品固有のエラーコードを適切な例外で投げてくれる
  代表的な実装クラスはJdbcTemplateとNamedParameterJdbcTemplate

### 8.2.2 画面などの作成

- *ここで使っているth:includeあたりとlayout dialectの使い分けってどうすればいいのか？*
- *HomeController.javaの中でUserServiceをDIしているが、この時点ではUserService作っていないので、エラーになる*
  
#### DTO
  
- コントローラクラスやサービスクラスなどの間でやり取りするためのクラスをDomainObjectといったり、DTO(DataTransferObject)という
- テーブルのカラムをフィールドに持つためのクラス

#### DAO

- リポジトリ実装クラスを簡単に切り替えるためのインタフェース

## 8.3

### JdbcTemplateのメソッド

- update: 登録、更新、削除に用いる。戻り値は登録したレコード数
- queryForObject: カウントの結果やカラムを1つだけ取得する場合に用いる。戻り値は取得したレコード数。
- queryForList: 複数件のselectに用いる。戻り値の型にはList<Map<String, Object>>を指定。Listが行、Mapが列を表す。
- queryForMap: レコード1件取得。戻り値はMap<String, Object>型。

### 8.3.3

- 動的なURLに対応したメソッドを作るためには、@GetMapping(/userDetail/{id})のようにする
- @PathVariableをつけると、渡されてきたURLの値を引数の変数に入れられる

### 8.3.4

- 更新と削除でURLとHTTPメソッドが同じ場合、HTMLのname属性をコントローラメソッドの第２引数に指定することで、両者を分ける。　
- *このケースだったら更新は@PostMapping, 削除は@DeleteMappingを使えばいいのでは？と思わなくもない。*  
  *=> HTMLのformはGETとPOSTしかサポートしていない。そのためDELETEではなく、POSTを使っているんだと気付いた。*  
  *やろうとすればできるようだが、一般的にはPUT/DELETEはREST API向けのリクエストをマッピングする際に用いるらしい。*  
  *今回は書籍のままにしようと思う。*

### 8.3.6 RowMapper

- RowMapper: データベースのレコードと、Javaオブジェクトのマッピングを行うためのクラス。(ORマッパー)似たようなSELECT文がたくさん必要な際に使うことで、リポジトリクラスの記述が楽になったり、コードの可読性が上がったりする。
- @Qualifier: @Autowiredと@Qualifierを使用することで、どのBeanを使用するか指定できる

### 8.3.7 BeanPropertyRowMapper

- BeanPropertyRowMapper: データベースから取得してきたカラム名と同一のフィールド名がクラスにあれば、自動でマッピングをしてくれる。そのためにはカラム名はスネークケース、フィールド名はキャメルケースにする必要がある。

### 8.3.8 ResultSetExecutor

- 複数件のselect結果をオブジェクトにマッピングする場合に使う

### 8.3.9 RowCallbackHandlerの実装

- RowMapper, ResultSetExecutorと異なり、戻り値を返さない
- 時間のかかる処理の実行中、処理が終わるまで別のことができる
- ファイル出力やデータチェックに利用される

## 8.4 NamedParameterJdbcTemplateの実装

- JdbcTemplateではPreparedStatementでメソッドの引数の順番に注意が必要だったが、NamedParameterJdbcTemplateでは必要なくなる。
- *記述量多いけど、可読性上がる感じ？*

```java
    // JdbcTemplate
    // Userテーブルにデータを1件insert.
    @Override
    public int insertOne(User user) throws DataAccessException {

        // １件登録
        int rowNumber = jdbc.update(
                "INSERT INTO m_user(user_id," + " password," + " user_name," + " birthday," + " age," + " marriage,"
                        + " role)" + " VALUES(?, ?, ?, ?, ?, ?, ?)",
                user.getUserId(), user.getPassword(), user.getUserName(), user.getBirthday(), user.getAge(),
                user.isMarriage(), user.getRole());

        return rowNumber;
    }
```

```java
    // NamedParameterJdbcTemplate
    // Userテーブルにデータを1件insert.
    @Override
    public int insertOne(User user) {

        // SQL文
        String sql = "INSERT INTO m_user(user_id," + " password," + " user_name," + " birthday," + " age,"
                + " marriage," + " role)" + " VALUES(:userId," + " :password," + " :userName," + " :birthday,"
                + " :age," + " :marriage," + " :role)";

        // パラメーター
        SqlParameterSource params = new MapSqlParameterSource().addValue("userId", user.getUserId())
                .addValue("password", user.getPassword()).addValue("userName", user.getUserName())
                .addValue("birthday", user.getBirthday()).addValue("age", user.getAge())
                .addValue("marriage", user.isMarriage()).addValue("role", user.getRole());

        // SQL実行
        return jdbc.update(sql, params);
    }
```

# 9章 例外ハンドリング

## 9.1 開発前の準備

### Springで例外処理を実装する方法

1. @AfterThrowingアスペクトを使用する
2. コントローラークラス毎に例外ハンドリングを実装する
3. Webアプリケーション全体で共通の例外ハンドリングを実装する

## 9.2 エラーページの作成

### 2種類のエラーページ

1. 共通エラーページ
  + WhiteLabelの代わり。必ず用意すべき
2. HTTPエラー毎のエラーページ
  + エラー毎にメッセージを変えたい場合に利用する

### 9.2.1 共通エラーページ

- src/main/resources/template配下にerror.htmlを用意すればよい

### 9.2.2 HTTPエラー毎のエラーページ

- src/main/resources/template配下にerrorディレクトリを作成して404.htmlなどのファイルを用意すればよい

## 9.3 @AfterThrowingアスペクトでの例外処理

- アスペクトの例外処理は、例外発生後に実行されるため、コンソールへのログ出力ぐらいしかできない。
- *コンソールログへのログ出力確認できなかったけど…？*

## 9.4 コントローラクラス毎の例外処理

- コントローラクラス内に@ExceptionHandlerアノテーションをつけたメソッドを用意する

## 9.5 Webアプリケーション全体の例外処理

- @ControllerAdviceアノテーションを付けたクラスを用意する

# 10章 Springセキュリティ

### 10.3.2 ログイン処理の実装

- ログイン処理を追加するにはhttp.formLogin()にメソッドチェーンで条件を追加する

```java
        // ログイン処理
        http.formLogin().loginProcessingUrl("/login") // HTMLNのフォームタグのaction属性と一致させる
                .loginPage("/login") // コントローラクラスの@GetMappingの引数と一致させる
                .failureUrl("/login")
                .usernameParameter("userId") // inputタグのname属性と一致させる
                .passwordParameter("password") // inputタグのname属性と一致させる
                .defaultSuccessUrl("/home", true);
```

### 10.3.3 ユーザーデータの取得（DB）

- *しれっとデータソースをAutowiredしているが、データソースが何かよくわからない。。。application.propertiesの記載内容を見るとDB周りということはわかるが。。。*
- *ユーザーIDに空もしくは未登録のアドレスを入力するとエラーメッセージが表示され、その後消えなくなる。登録済アドレスを入力するとエラーメッセージは表示されない。この状態が正しいのかよくわからないが、とりあえず読み進める。*
- SpringBootはバージョン2からパスワードの暗号化が必須

### 10.3.4 パスワードの暗号化

- SpringにはPasswordEncoderというインタフェースが用意されている

### 10.3.5 ログアウト処理

- Springのデフォルトでは、ログアウト処理はPOSTメソッドで送る。GETメソッドに変えることもできる
- *logoutの処理はControllerに書かなくていいんだなぁ*

## 10.4 CSRF対策

### 10.4.1 CSRFとは

- CSRF対策のため、Springで作ったWebサイト(*Vue.jsで作ったサイトとか？*)から、トークンをパラメータに加えてサーバにリクエストするようにする（Springではデフォルトで有効）
- トークンが必要なのはGETメソッド以外

### 10.4.2 CSRF対策の実装

- 学習のためログイン画面でトークンを送る設定をしたが、th:action属性を使うとタイムリーフが自動でトークンを追加するので、設定不要
- *フロントをVue.jsで実装する際などは設定が必要そう*

## 10.5 認可

### 10.5.1 URLの認可

- URLの認可: 権限がないユーザにはURLにアクセスさせない
- Springではロール名の先頭に"ROLE_"を付けるルールになっている

### 10.5.2 画面表示の認可

- 画面表示の認可: 権限がないユーザの画面に項目を表示しない
- *thymeleaf-extras-springsecurity4だと一般権限でもアドミン用画面が見えてしまって、pom.xmlとhomeLayout.htmlをthymeleaf-extras-springsecurity5にしたら動いた。[参考](https://github.com/thymeleaf/thymeleaf-extras-springsecurity/issues/61)*

# 11 RESTサービス

### 11.2.3

- ディレクトリ構成のWebConfig.javaがなくて、なぜないか探したところ、6章のバリデーションで、独自メッセージを扱う時に用意するメッセージで独自メッセージ扱わないなら不要のファイルだった。なので特に問題なし。
- @RestControllerを付けると、各メソッドの戻り値がHTMLファイルを探さなくなる
- *RESTサービスのみCSRF対策を無効にするとのことだが、なんで？実サービスでの無効化はありなのか？*

### 11.3.2

- @RequestBodyを使うと、HTTPリクエストのボディ部分をコントローラメソッドの引数にマッピングしてくれるので、POSTメソッドでもHTTPボディを受け取ることができる

```bash
curl -X POST -H 'Content-Type:application/json' -d '{"userId":"tamura@co.jp","password":"pass","userName":"tamura","birthday":"1986-11-05","age":"31","marriage":"false","role":"ROLE_ADMIN"}' http://localhost:8080/rest/insert
```

### 11.3.3

```bash
curl -X PUT -H 'Content-Type:application/json' -d '{"userId":"tamura@co.jp","password":"pass","userName":"tamura","birthday":"1986-11-05","age":"32","marriage":"false","role":"ROLE_ADMIN"}' http://localhost:8080/rest/update
```

### 11.3.4

```bash
curl http://localhost:8080/rest/delete/tamura@co.jp -X DELETE
```

- @ResponseBodyをメソッドにつけると、戻り値をHTMLではなく、JSONなどに変更できる。RESTサービス以外に、JavaScriptからSpringにリクエストを投げて、非同期処理する際に用いる。

# 12 Springテスト

## 12.1 SprintTestとは

- SpringTest: Spring起動後にJUnitなどのテストフレームワークを動かすことができるSpringのフレームワーク。  
  （SpringBootを起動しないとBeanが作られず、コントローラクラスやサービスクラスが動かない。）  
  使い方は、テスト用のクラスにSpring用のアノテーションをいくつか付けるだけで、あとはJUnitそのまま。

### 12.2.1 開発内容の説明

- リポジトリクラスのテスト: 簡単。リポジトリクラス特有のテストもある
- コントローラクラスのテスト: 画面表示内容の確認と、モックを使ったテストを実施。ログイン後に表示される表示される画面のテストについても学ぶ

## 12.3 リポジトリークラスのテスト

### 12.3.1

- @RunWith(SpringRunner.class): @RunWithはテストを何で実行するか指定できる。SpringRunnerはSpring用のJUnitを使えるクラス
- @SpringBootTest: SpringBootを起動してからテストを始める

### 12.3.2

- @Sql: 引数のSQLを実行後にテストを走らせる。そのメソッドのみ有効。

# 13 Spring + MyBatis

## 13.1 MyBatisとは

- MyBatisはSQLとオブジェクトのマッピングを設定ファイルで行うライブラリ。JDBCと競合するレイヤの技術
- SpringでMyBatisを使う場合、アノテーション内にSQLを書く方法と設定ファイル内にSQLを書く方法がある。基本は設定ファイルを使う。DB製品に応じた切り替えが楽だから