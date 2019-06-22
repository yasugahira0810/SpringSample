# SpringMVC

## URLの構成

```
http://<サーバー名>:<ポート>/コンテキストルート/<コントローラメソッドの戻り値>
```

コントローラメソッドの戻り値は、ビューのパスから拡張子を除いたもの。

# Thymeleaf

- th:で始まる属性を使い、サーバ実行時に埋め込まれる値を記述
- ブラウザで直接起動した際はth:で始まる属性はブラウザに無視される

## redirect

- redirectの設定してURLを変えようとすると404になりまくっていたけど、わかってきた気がする。
- 以下の通り設定すると、loginクリックしてPOSTメソッド実行した時の結果は以下の通りとなる。
  + URLはlocalhost:8080/ababa
  + 読み込まれるファイルはtemplates/ababa/ababa.html
- 「return "redirect:ababa";」と「@GetMapping("/ababa")」のababaをababa/ababaにすると、以下の通りとなる。
  + URLはlocalhost:8080/ababa/ababa
  + 読み込まれるファイルはtemplates/ababa/ababa.html

```java
    /**
     * ログイン画面のPOSTメソッド用処理.
     */
    @PostMapping("/login")
    public String postLogin(Model model) {

        System.out.println("##########   postLogin Method executed   ##########");
        // login.htmlに画面遷移
        return "redirect:ababa";
    }

    /**
     * ログイン画面のPOSTメソッド用処理.
     */
    @GetMapping("/ababa")
    public String postAbaba(Model model) {

        System.out.println("##########   postLogin Method executed   ##########");
        // login.htmlに画面遷移
        return "ababa/ababa";
    }
```