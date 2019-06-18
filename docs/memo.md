## 3-1

- HelloController.javaというコントローラを作成しているけど、このコントローラを参照させるための設定はしていない。  
  @Controllerのおかげなのか@GetMappingのおかげなのかよくわからない。

## 3-2

### *HTMLのform => Controllerの値の受け渡し*

- HTML: th:value属性を使うことで、画面からControllerクラスに値を渡すことができる。POST先をformタグのaction要素に指定。
- Controller: @PostMappingアノテーションをつけたメソッドの引数に@RequestParamを付けることで、HTMLからの入力内容を受け取れる。  
  アノテーションの引数には（多分th:value属性を指定した箇所の）name属性を指定する。
- formタグのaction要素と同じ値を@PostMappingの引数に指定してメソッドを作成する。

### *Controller => HTMLの画面の値の受け渡し*

- Controller: model.Attributeに任意のキー、HTMLの入力内容を指定した値をセットする。
- HTML: th:text属性にmodel.Attributeで登録したキーを指定することで、Controllerから値を受け取れる。

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