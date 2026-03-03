# Regexer-Editor

![Logo](previews/regexer_project_logo.png)

A simple and easy-to-use 📱Android app built in *Jetpack Compose* and based on Kotlin **Regex** library for testing regular expressions.📃

## 📚Used libs:
 * dagger-hilt
 * room
 * androidx datastore
 * viewmodel
 * <a href="https://github.com/skydoves/colorpicker-compose">colorpicker-compose</a>

 ## 🖼Gallery (vertical and horizontal, light and dark themes)
 <table>
    <tr>
        <td>
            <img src="previews\regexer1.jpg">
        </td>
        <td>
            <img src="previews\regexer2.jpg">
        </td>
        <td>
            <img src="previews\regexer3.jpg">
        </td>
        <td>
            <img src="previews\regexer5.jpg">
        </td>
        <td>
            <img src="previews\regexer6.jpg">
        </td>
        <td>
            <img src="previews\regexer7.jpg">
        </td>
        <td>
            <img src="previews\regexer4.jpg">
        </td>
    </tr>
</table>

## 🔃Android versions
Android **8.0** or later

## 🌟Features
* 📲dynamic representation of the result of a regular expression
* 💾ability to save regular expressions
* 🎨select the color for highlighting matches
* 📤loading test text from the buffer or a text file from files
* 📣display exceptions
* 📄background information

## 📃How to use ?
The application has two input fields: the top one is for regular expressions (syntax highlighting is available), and the bottom one is large for test text. However, there is a **2500 character limit** for the application to work smoothly. The test text should not be too large when using a large input field, a toolbar is activated that provides the following options: full text clearing, loading text from the buffer, changing the highlight color of the match, and loading a text file.The top field is also rich in functionality. By clicking on the gear, you can select the necessary flags and also change the global search mode to 1 entry, also clear the text and view a list of all matches. The header contains a button for help with regular expressions and another menu button where you can read information about the application, view a list of saved regular expressions (you can also view it by swiping right to open a modal dialog), and exit.