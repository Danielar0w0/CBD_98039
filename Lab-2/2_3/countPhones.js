// load("C:/Users/DanielaDias/Documents/Uni/CBD/Lab-2/2_3/countPhones.js")

countPhones = function () {

    var cursor = db.phones.aggregate( [ { $group: { _id: "$components.prefix"} }, 
        { $count: "nPrefixes" }
    ] )

    // Alternative 1
    // cursor.forEach(printjson);
    
    // ALternative 2
    // var documentArray = cursor.toArray()
    // print(tojson(documentArray[0]))

    // Alternative 3
    var documentElem = cursor.toArray()[0]
    print("Number of prefixes: " + tojson(documentElem["nPrefixes"]))
}