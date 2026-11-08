# Minimal Launcher

Launcher Android nativa, sem bibliotecas externas, sem pesquisa, sem favoritos e sem serviços em segundo plano.

## Recursos
- Grade fixa de 4 colunas.
- Todos os aplicativos que oferecem ícone de inicialização aparecem na tela inicial.
- Ordenação alfabética em português (A–Z).
- Atualiza a lista ao retornar à tela inicial.
- Abre o aplicativo ao tocar no ícone.
- Interface escura e sem animações adicionadas.
- Sem acesso à internet, anúncios, analytics ou banco de dados.

## Gerar o APK
1. Instale o Android Studio e o Android SDK Platform 35.
2. Abra a pasta `MinimalLauncher` no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Selecione **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. O APK de depuração será gerado em `app/build/outputs/apk/debug/app-debug.apk`.
6. Instale o APK e escolha **Minimal Launcher** como aplicativo de início padrão.

## Observações
- O projeto usa apenas APIs nativas do Android; não adiciona dependências de bibliotecas.
- O consumo real de RAM depende da quantidade de aplicativos, dos ícones fornecidos por cada app e da versão do Android.
- A lista inclui atividades que os próprios aplicativos expõem como iniciáveis. Alguns apps podem não aparecer se não oferecerem uma atividade de launcher.
- O Android pode manter o processo da launcher em memória para acelerar o retorno à tela inicial; isso é comportamento normal do sistema.
