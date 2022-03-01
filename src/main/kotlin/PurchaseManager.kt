import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

fun main(args: Array<String>) {

    val purchasedProductNames = arrayOf("CVCD", "SDFD", "DDDF", "SDFD")
    val mappingsStr = "{ \"CVCD\": { \"version\": 1, \"edition\": \"X\" }, \"SDFD\": { \"version\": 2, \"edition\": \"Z\" }, \"DDDF\": { \"version\": 1 } }"

    val aggregatedPurchasedProducts = aggregatedPurchasedProducts(purchasedProductNames, mappingsStr)
    println(aggregatedPurchasedProducts)

}

/**
 * Returns an aggregated list of purchased products based on input
 * @param purchasedProductNames an Array list containing product names
 * @param mappingsStr a JSON String with following syntax { "<productName>": { "version": int, "edition": String }, "<ProductName>": { "version": int, "edition": String }, ...}
 * The method returns an aggregated list of purchased products and quantity as following JSON array: [ { "version": int, "edition": String, "quantity": int }, ... ]
 */
fun aggregatedPurchasedProducts(purchasedProductNames: Array<String>, mappingsStr: String) : String {

    val mappings: Map<String, ProductSpecs>

    /*
        The TypeToken class returns an instance of ParameterizedTypeImpl that preserves the type of the key and value even at runtime
        And No, with all honestly, I didn't come up with this solution. First tried my own solutions which did not look as clean as this
        So I headed over to google and digged until I found a better solution. More info here: https://www.baeldung.com/gson-json-to-map
        A good software engineer knows that Internet knows it better :D  ;)
     */
    try {
        val mapType: Type = object : TypeToken<Map<String?, ProductSpecs?>?>() {}.getType()
        mappings = Gson().fromJson(mappingsStr, mapType)
    }catch (exception: Exception){
        throw IllegalArgumentException("Uppsss... Your input parameters are not valid but go ahead, Try again! I have all the time in the world. Well unless world is ending...")
    }

    val products = mutableListOf<Product>()

    mappings.forEach { mapping ->
        val productName = mapping.key
        var productSpecs = mapping.value

        val product = Product(productName, productSpecs.version, productSpecs.edition)
        products.add(product)
    }

    purchasedProductNames.forEach { purchasedProductName ->
        for (product in products) {
            if (product.name == purchasedProductName){
                product.quantity = product.quantity.inc()
            }
        }
    }

    val gson = GsonBuilder().setPrettyPrinting().create()


    return gson.toJson(products)
}

private class Product(
    @Transient var name: String,
    var version: Int,
    var edition: String?,
    var quantity: Int = 0
)

private data class ProductSpecs(
    var version: Int,
    var edition: String
)


