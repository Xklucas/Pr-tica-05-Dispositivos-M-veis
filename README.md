<p align="center">
  <img src="docs/checkin-cover.jpg" alt="Check-in Locais" width="100%">
</p>

# Check-in Locais

Aplicativo Android desenvolvido como atividade da disciplina de Dispositivos Móveis. O sistema registra check-ins utilizando a localização do aparelho e organiza as visitas por local e categoria.

## Funcionalidades

- Leitura da localização atual do dispositivo
- Cadastro e atualização de check-ins
- Organização dos locais por categoria
- Exibição dos pontos em mapa
- Gerenciamento dos registros salvos
- Relatório de visitas
- Armazenamento local com SQLite

## Tecnologias

- Java
- Android SDK
- SQLite
- Google Maps SDK
- Google Play Services Location

## Estrutura principal

```text
app/src/main/java/com/example/checkinlocais/
├── MainActivity.java
├── MapaCheckinActivity.java
├── GestaoCheckinActivity.java
├── RelatorioActivity.java
├── DatabaseHelper.java
├── Checkin.java
└── Categoria.java
```

## Como executar

1. Clone o repositório e abra a pasta no Android Studio.
2. Copie `local.properties.example` para `local.properties`.
3. Informe o caminho do Android SDK e sua chave do Google Maps:

```properties
sdk.dir=C:\\Users\\SEU_USUARIO\\AppData\\Local\\Android\\Sdk
MAPS_API_KEY=SUA_CHAVE_DO_GOOGLE_MAPS
```

4. Sincronize o Gradle.
5. Execute o aplicativo em um dispositivo ou emulador com Android 5.0 ou superior.

O aplicativo precisa de permissão de localização para registrar check-ins e exibir o mapa.

## Contexto acadêmico

Projeto desenvolvido para a disciplina INF 311 — Programação para Dispositivos Móveis, na Universidade Federal de Viçosa.
