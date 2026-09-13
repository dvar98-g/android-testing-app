# Android Testing App — Mobile Automation Final Practice

Framework de automatización mobile sobre la [WebdriverIO Native Demo App](https://github.com/webdriverio/native-demo-app), construido con Appium + Java + TestNG siguiendo el patrón Page Object (Screens).

---

## Stack

| Componente | Versión |
|---|---|
| Java | 17 |
| Maven | 3.x |
| Appium Java Client | 10.0.0 |
| TestNG | 7.8.0 |
| Appium Server | 2.x |
| Motor de automatización | UiAutomator2 |

---

## Requisitos previos

1. **JDK 17** instalado y `JAVA_HOME` configurado.
2. **Android Studio** con el SDK y un emulador (AVD) creado.
3. **Appium Server 2.x** con el driver UiAutomator2:
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
4. **APK de la app** descargado desde las [releases del repo oficial](https://github.com/webdriverio/native-demo-app/releases) (usar el archivo `.apk`).

---

## Puesta en marcha

### 1. Iniciar el emulador

Desde Android Studio (Device Manager) o por terminal:

```bash
emulator -avd <nombre_del_avd>
```

Verificar que quedó conectado y anotar su identificador:

```bash
adb devices
# Ej: emulator-5554   device
```

### 2. Instalar la app

```bash
adb install /ruta/al/android.wdio.native.app.apk
```

Confirmar la instalación:

```bash
adb shell pm list packages | grep wdiodemoapp
```

### 3. Levantar el servidor Appium

En una terminal aparte, y dejarla corriendo:

```bash
appium
```

Por defecto queda escuchando en `http://127.0.0.1:4723`.

### 4. Configurar el proyecto

Copiar el template y completarlo con los valores del entorno local:

```bash
cp config.template.properties src/test/resources/config.properties
```

`config.properties` está en `.gitignore` y nunca se commitea. El template versionado sirve como referencia de todas las propiedades disponibles.

---

## Configuración

Toda la configuración está externalizada. `ConfigReader` resuelve cada valor con la siguiente prioridad:

**variable de entorno → `config.properties` → valor por defecto**

Esto permite ejecutar sin archivo de configuración (por ejemplo en CI/CD) siempre que las variables de entorno cubran los valores requeridos.

| Propiedad | Variable de entorno | Requerida | Default |
|---|---|---|---|
| `app.path` | `APP_PATH` | Sí | — |
| `device.name` | `DEVICE_NAME` | Sí | — |
| `udid` | `UDID` | Sí | — |
| `platform.version` | `PLATFORM_VERSION` | Sí | — |
| `platform.name` | `PLATFORM_NAME` | No | `Android` |
| `automation.name` | `AUTOMATION_NAME` | No | `UiAutomator2` |
| `appium.server.url` | `APPIUM_SERVER_URL` | No | `http://127.0.0.1:4723` |
| `explicit.wait.seconds` | `EXPLICIT_WAIT_SECONDS` | No | `15` |
| `test.email.domain` | `TEST_EMAIL_DOMAIN` | No | `test.com` |
| `test.password` | `TEST_PASSWORD` | No | `Test1234!` |

`app.path` debe ser una **ruta absoluta** al `.apk`.

Si falta un valor requerido en ambas fuentes, el framework falla al inicio con un mensaje explícito indicando qué propiedad falta, en vez de dejar que Appium falle más adelante con un error críptico.

---

## Ejecución

**Suite completa:**

```bash
mvn test
```

**Una clase puntual:**

```bash
mvn test -Dtest=SwipeTest
```

También puede ejecutarse desde IntelliJ IDEA con click derecho sobre `testng.xml` o sobre cualquier clase de test.

La ejecución es **secuencial**, de forma deliberada: cada método de test abre su propia sesión de Appium contra un único emulador, por lo que la ejecución en paralelo generaría conflictos de sesión sobre el mismo dispositivo.

---

## Escenarios cubiertos

### 1. Navegación en la bottom tab bar — `MenuTest` (6 tests)

Precondición: la app en la pantalla Home. Cada test toca un ícono distinto de la tab bar y verifica que los elementos principales de la pantalla destino estén desplegados.

| Test | Verifica |
|---|---|
| `navigateToWebview_displaysWebviewScreen` | WebView presente y branding de WebdriverIO |
| `navigateToLogin_displaysLoginScreen` | Inputs de email/password y botón de login |
| `navigateToForms_displaysFormsScreen` | Input, switch, dropdown y botones Active/Inactive |
| `navigateToSwipe_displaysSwipeScreen` | Carrusel presente |
| `navigateToDrag_displaysDragScreen` | Botón de reinicio del puzzle |
| `navigateToMenu_opensMenuPanel` | Panel lateral abierto con sus ítems |

Home no tiene test propio: la app arranca ahí y su correcta visualización se verifica como precondición de los seis tests.

### 2. Sign Up exitoso — `SignUpTest` (1 test)

Navega al formulario de Sign Up, lo completa con un **email aleatorio** generado en cada ejecución (`RandomUtils`) y verifica el alert nativo de confirmación.

El email aleatorio permite reejecutar el test las veces que haga falta sin colisiones. No se requiere limpieza posterior: la app no persiste cuentas ni valida credenciales contra un backend.

### 3. Login exitoso — `LoginTest` (1 test)

Precondición: usuario previamente creado. El `@BeforeMethod` ejecuta un signup real reutilizando los métodos de `LoginScreen`, y recién entonces el test hace login con esas credenciales y verifica el alert de éxito.

El test **no depende** de que `SignUpTest` haya corrido antes: arma su propia precondición desde cero.

### 4. Swipe de cards — `SwipeTest` (5 tests)

| Test | Verifica |
|---|---|
| `carousel_startsOnFirstCard` | Estado inicial del carrusel |
| `swipeOnce_showsSecondCard_andHidesFirst` | Al avanzar, el card anterior deja de estar visible |
| `swipeThroughAllCards_showsEachCardInOrder` | Recorrido completo de los 6 cards en orden |
| `swipeToLastCard_leavesOnlyThatCardRendered` | En el último card queda un solo item renderizado |
| `swipeVertically_revealsHiddenText` | El texto oculto "You found me!!!" tras scrollear |

---

## Arquitectura

```
src/test/
├── java/com/globant/mobile/
│   ├── config/
│   │   └── ConfigReader.java              # Resolución de configuración por capas
│   ├── utils/
│   │   ├── BaseTest.java                  # Capabilities y ciclo de vida de la sesión Appium
│   │   ├── BaseScreen.java                # Esperas explícitas, taps, gestos y locators base
│   │   └── RandomUtils.java               # Generación de datos únicos por ejecución
│   ├── screens/
│   │   ├── common/
│   │   │   ├── BottomTabBarComponent.java # Tab bar (compartida por todas las pantallas)
│   │   │   ├── NativeAlertComponent.java  # AlertDialog nativo de Android
│   │   │   └── ScreenNames.java           # Constantes de accessibility ids de la tab bar
│   │   ├── home/
│   │   │   └── HomeScreen.java
│   │   ├── webview/
│   │   │   └── WebviewScreen.java
│   │   ├── login/
│   │   │   └── LoginScreen.java
│   │   ├── forms/
│   │   │   └── FormsScreen.java
│   │   ├── swipe/
│   │   │   └── SwipeScreen.java
│   │   ├── drag/
│   │   │   └── DragScreen.java
│   │   └── menu/
│   │       ├── MenuScreen.java
│   │       └── MenuScreenKeys.java
│   └── tests/
│       ├── menu/
│       │   └── MenuTest.java
│       ├── login/
│       │   ├── SignUpTest.java
│       │   └── LoginTest.java
│       └── swipe/
│           └── SwipeTest.java
└── resources/
    └── suites/
        └── testng.xml                     # Configuración de ejecución
```

### Decisiones de diseño

**Screens por composición.** Cada Screen recibe el driver por constructor en vez de heredarlo del test. Los Screens no contienen aserciones: exponen estado (`isXDisplayed()`, `getXText()`) y las validaciones viven en las clases de test.

**Solo esperas explícitas.** No se usa wait implícito (`implicitlyWait` queda explícitamente en cero), para evitar la interacción impredecible entre ambos tipos de espera.

**Sesión por test.** `@BeforeMethod` abre una sesión nueva con `noReset=false`, garantizando que cada test arranque con la app en estado limpio y sea completamente independiente del resto.

**Locators sobre accessibility ids.** Se prioriza `content-desc` mediante `UiSelector().description(...)`. Se recurre a `resourceId` o `text` solo cuando el elemento no expone accessibility id propio.

---

## Notas sobre gestos en React Native

Durante el desarrollo se verificó experimentalmente que esta app requiere un tipo de gesto distinto según el componente. Quedan documentados porque no son evidentes y condicionan cualquier extensión del framework:

| Componente | Gesto requerido | Por qué |
|---|---|---|
| Botones (`Pressable`) | W3C Actions con presión prolongada y micro-movimiento | `click()` se resuelve como acción de accesibilidad y el `onPress` no se dispara; el comando responde OK pero la app no reacciona |
| Carrusel con snap | `mobile: flingGesture` | Un arrastre puede quedar a mitad de camino entre dos cards; el fling imprime la velocidad necesaria para completar la transición |
| ScrollView de pantalla | Arrastre lento (~1s) fuera del área del carrusel | Un arrastre rápido no produce movimiento; y si el gesto arranca sobre el carrusel, este lo captura sin propagarlo al contenedor padre |

Además, tras un scroll es necesario esperar a que la inercia se asiente antes del siguiente toque: el responder system de React Native cancela los toques que llegan inmediatamente después de un gesto de scroll.