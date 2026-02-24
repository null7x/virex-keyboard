# 📱 Сборка APK - Инструкция

**Статус**: Процесс сборки запущен...  
**Дата**: 23 февраля 2026

---

## 🚀 Автоматическая Сборка (В ПРОЦЕССЕ)

Запущена команда:
```bash
gradle assembleDebug
```

**Примерное время**: 2-5 минут (первая сборка)

APK будет находиться в:
```
ultra-keyboard/app/build/outputs/apk/debug/app-debug.apk
```

---

## 🛠️ Ручная Сборка (Альтернатива)

### Вариант 1: Через Android Studio (РЕКОМЕНДУЕТСЯ)

1. **Откройте проект**:
   ```
   File → Open → D:\github\ket\ultra-keyboard
   ```

2. **Дождитесь Gradle Sync** (2-3 минуты)

3. **Исправьте ошибки** (если есть):
   - Unresolved reference: android → Gradle sync решит
   - Compose compiler → уже исправлено в build.gradle.kts

4. **Соберите APK**:
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```

5. **APK будет готов**:
   ```
app/build/outputs/apk/debug/app-debug.apk
   ```

---

### Вариант 2: Через Command Line

#### Windows PowerShell/CMD:
```bash
cd D:\github\ket\ultra-keyboard

# Если gradlew.bat НЕ существует:
gradle wrapper --gradle-version=8.7

# Соберите Debug APK:
.\gradlew.bat assembleDebug

# Или Release APK (с подписью):
.\gradlew.bat assembleRelease
```

#### После завершения:
```
✅ Debug APK: app\build\outputs\apk\debug\app-debug.apk
✅ Release APK: app\build\outputs\apk\release\app-release-unsigned.apk
```

---

## 📊 Размер APK

**Ожидаемый размер**:
- **Debug APK**: ~30-35 MB (с debug info)
- **Release APK**: ~25-28 MB (minified)
- **Target**: <35 MB (соответствует спеке)

---

## 🔧 Решение Проблем

### Проблема 1: "Compose Compiler plugin required"
**Решение**: ✅ Исправлено! Добавлен `org.jetbrains.kotlin.plugin.compose`

### Проблема 2: "Unresolved reference: android"
**Причина**: Gradle sync еще не выполнен  
**Решение**: Откройте в Android Studio, дождитесь sync

### Проблема 3: "gradlew not found"
**Решение**: Используйте `gradle` напрямую или создайте wrapper:
```bash
gradle wrapper --gradle-version=8.7
```

### Проблема 4: Долгая сборка
**Первая сборка**: 3-5 минут (скачивает зависимости)  
**Последующие**: 30-60 секунд

---

## 🎯 Что Делать После Сборки

### 1. Установите APK на устройство
```bash
# Через ADB:
adb install app/build/outputs/apk/debug/app-debug.apk

# Или перенесите файл на телефон и установите вручную
```

### 2. Включите клавиатуру
```
Settings → System → Languages & input → 
On-screen keyboard → Manage keyboards → 
Enable "Virex Keyboard"
```

### 3. Выберите Virex Keyboard
Нажмите в любом текстовом поле, выберите Virex Keyboard

### 4. Тестируйте! 🎉
- ⌨️ Typing (EN/RU)
- 🎨 Apply themes
- 😀 Emoji panel
- 🌈 RGB effects (PRO themes)
- ✍️ Custom fonts (PRO)

---

## 📦 Деплой Backend (Тоже Нужен!)

### Вариант 1: Через Vercel Dashboard (Проще)

1. Перейдите на https://vercel.com/new
2. Импортируйте GitHub репо или загрузите `backend/` папку
3. Deploy → Готово!
4. Обновите `baseUrl` в `app/src/main/java/com/virex/app/di/AppModule.kt`:
   ```kotlin
   private const val BASE_URL = "https://ваш-домен.vercel.app/"
   ```

### Вариант 2: Через CLI (если установлен)
```bash
cd backend
npm install
npx vercel --prod
```

---

## ✅ Финальный Чеклист

- [ ] APK собран успешно
- [ ] APK размер < 35 MB
- [ ] Backend задеплоен на Vercel
- [ ] baseUrl обновлён в AppModule.kt
- [ ] APK установлен на тестовое устройство
- [ ] Клавиатура включена в настройках
- [ ] Themes загружаются из backend
- [ ] RGB effects работают (60 FPS)
- [ ] Custom fonts применяются

---

## 🚀 Следующие Шаги

1. **QA Testing** (3 дня)
   - Performance на разных устройствах
   - Battery drain testing  
   - Memory leaks проверка

2. **Google Play Console Setup** (1 день)
   - Создать App Listing
   - Загрузить screenshots  
   - Настроить In-App Products (PRO subscriptions)

3. **Soft Launch** (1-2 недели)
   - Запуск в 1-2 странах
   - Monitor retention/crash rate
   - Iterate на feedback

4. **Global Launch** 🎉
   - Worldwide release
   - Marketing push
   - Monitor KPI (D1/D7 retention, ARPDAU)

---

**💡 Совет**: Если сборка через командную строку зависла, используйте Android Studio - это быстрее и надёжнее для первой сборки.

**🎊 Проект готов к запуску! Осталось только собрать APK и задеплоить backend!**
