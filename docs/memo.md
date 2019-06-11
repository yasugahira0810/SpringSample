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
