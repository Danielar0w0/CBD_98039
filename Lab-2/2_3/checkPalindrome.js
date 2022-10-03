// load("C:/Users/DanielaDias/Documents/Uni/CBD/Lab-2/2_3/checkPalindrome.js")

checkPalindrome = function () {

    var cursor = db.phones.find( {}, { _id: 1 } )
    var documentArray = cursor.toArray()

    print("Palindromes:")
    for (const i in documentArray) {
        var str = documentArray[i]["_id"]

        var number = str.toString().substring(3, str.length)

        if (number == number.toString().split('').reverse().join(''))
            print(number)
    }
}