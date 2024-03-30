package csci5708.mobilecomputing.healthharvest
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import csci5708.mobilecomputing.healthharvest.DataModels.FoodItem
import java.time.LocalDateTime
import java.time.ZoneId

class FoodDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Existing columns
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DATE_TAKEN = "date_taken"
        private const val COLUMN_CALORIES = "calories"

        // New columns
        private const val COLUMN_QUANTITY = "quantity"

        private const val DATABASE_VERSION = 3 // Increment the version for the schema change
        private const val DATABASE_NAME = "food_database"
        private const val TABLE_NAME = "food_items"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_DATE_TAKEN INTEGER, " +
                "$COLUMN_CALORIES INTEGER, " +
                "$COLUMN_QUANTITY INTEGER" +
                ")"
        db.execSQL(createTableQuery)

        insertDefaultFoodItems(db)
    }

    fun insertDefaultFoodItems(db: SQLiteDatabase) {
        val defaultFoodItems = listOf(
            FoodItem(
                id = 1,
                name = "Apple",
                DateTaken = 0,
                calories = 52,
                quantity = 1
            ),
            FoodItem(
                id = 2,
                name = "Banana",
                DateTaken = 0,
                calories = 89,
                quantity = 1
            )
        )

        for (foodItem in defaultFoodItems) {
            val contentValues = ContentValues()
            contentValues.put(COLUMN_NAME, foodItem.name)
            contentValues.put(COLUMN_DATE_TAKEN, foodItem.DateTaken)
            contentValues.put(COLUMN_CALORIES, foodItem.calories)
            contentValues.put(COLUMN_QUANTITY, foodItem.quantity)

            db.insert(TABLE_NAME, null, contentValues)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertFoodItem(foodItem: FoodItem): Long {
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME, foodItem.name)
        contentValues.put(COLUMN_DATE_TAKEN, foodItem.DateTaken) // Updated field name
        contentValues.put(COLUMN_CALORIES, foodItem.calories)
        contentValues.put(COLUMN_QUANTITY, foodItem.quantity) // New field

        val db = this.writableDatabase
        return db.insert(TABLE_NAME, null, contentValues)
    }


    fun getAllFoodItemsForToday(): List<FoodItem> {
        val foodList = mutableListOf<FoodItem>()
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_DATE_TAKEN, COLUMN_CALORIES, COLUMN_QUANTITY) // Add the new columns

        val selection = "$COLUMN_DATE_TAKEN between ? and ?"
        // get the start and end of today in milliseconds using system time
        val millisecondsStartOfToday = localDateTimeToMilliseconds(LocalDateTime.of(
            LocalDateTime.now().year,
            LocalDateTime.now().month,
            LocalDateTime.now().dayOfMonth,
            0,
            0,
            0
        ))

        val millisecondsEndOfToday = localDateTimeToMilliseconds(LocalDateTime.of(
            LocalDateTime.now().year,
            LocalDateTime.now().month,
            LocalDateTime.now().dayOfMonth,
            23,
            59,
            59
        ))

        val selectionArgs = arrayOf(millisecondsStartOfToday.toString(), millisecondsEndOfToday.toString())

        val cursor: Cursor? = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null) // Updated query to include the new columns

        cursor?.let {
            val idIndex = it.getColumnIndex(COLUMN_ID)
            val nameIndex = it.getColumnIndex(COLUMN_NAME)
            val dateTakenIndex = it.getColumnIndex(COLUMN_DATE_TAKEN)
            val caloriesIndex = it.getColumnIndex(COLUMN_CALORIES)
            val quantityIndex = it.getColumnIndex(COLUMN_QUANTITY) // New field

            while (it.moveToNext()) {
                val id = if (idIndex != -1) it.getLong(idIndex) else -1
                val name = if (nameIndex != -1) it.getString(nameIndex) else ""
                val dateTaken = if (dateTakenIndex != -1) it.getLong(dateTakenIndex) else 0
                val calories = if (caloriesIndex != -1) it.getInt(caloriesIndex) else 0
                val quantity = if (quantityIndex != -1) it.getInt(quantityIndex) else 0 // New field

                foodList.add(FoodItem(id, name, dateTaken, calories, quantity)) // Include the new fields in the FoodItem object
            }

            it.close()
        }

        return foodList
    }


    fun deleteFoodItem(id: Long): Int {
        val db = this.writableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        // Delete the row with the given id from the table
        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    fun getAllFoodNames(): Array<String> {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_NAME)

        val cursor: Cursor? = db.query(TABLE_NAME, columns, null, null, null, null, null)
        cursor?.let {
            val nameIndex = it.getColumnIndex(COLUMN_NAME)
            val foodNames = mutableListOf<String>()

            while (it.moveToNext()) {
                val name = if (nameIndex != -1) it.getString(nameIndex) else ""
                foodNames.add(name)
            }

            it.close()
            return foodNames.toTypedArray()
        }

        return emptyArray() // Return an empty array if there are no food items in the database
    }


    fun getFoodItem(id: Long): FoodItem? {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_DATE_TAKEN, COLUMN_CALORIES, COLUMN_QUANTITY) // Add the new columns

        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor: Cursor? = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val nameIndex = it.getColumnIndex(COLUMN_NAME)
                val dateTakenIndex = it.getColumnIndex(COLUMN_DATE_TAKEN)
                val caloriesIndex = it.getColumnIndex(COLUMN_CALORIES)
                val quantityIndex = it.getColumnIndex(COLUMN_QUANTITY) // New field

                val id = if (idIndex != -1) it.getLong(idIndex) else -1
                val name = if (nameIndex != -1) it.getString(nameIndex) else ""
                val dateTaken = if (dateTakenIndex != -1) it.getLong(dateTakenIndex) else 0
                val calories = if (caloriesIndex != -1) it.getInt(caloriesIndex) else 0
                val quantity = if (quantityIndex != -1) it.getInt(quantityIndex) else 0 // New field

                return FoodItem(id, name, dateTaken, calories, quantity) // Include the new fields in the FoodItem object
            }
            it.close()
        }
        return null // Return null if the food item with the given id is not found
    }

    fun updateFoodItem(foodItem: FoodItem): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME, foodItem.name)
        contentValues.put(COLUMN_DATE_TAKEN, foodItem.DateTaken)
        contentValues.put(COLUMN_CALORIES, foodItem.calories)
        contentValues.put(COLUMN_QUANTITY, foodItem.quantity) // New field

        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(foodItem.id.toString())

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

    fun localDateTimeToMilliseconds(localDateTime: LocalDateTime): Long {
        val zoneId = ZoneId.systemDefault() // You can use a specific ZoneId if needed
        val zonedDateTime = localDateTime.atZone(zoneId)
        val instant = zonedDateTime.toInstant()
        return instant.toEpochMilli()
    }

    fun getTotalCaloriesToday(): Int {
        val db = this.readableDatabase
        val columns = arrayOf("SUM($COLUMN_CALORIES * $COLUMN_QUANTITY)")


        val selection = "$COLUMN_DATE_TAKEN between ? and ?"
        // get the start and end of today in milliseconds using system time
        val millisecondsStartOfToday = localDateTimeToMilliseconds(LocalDateTime.of(
            LocalDateTime.now().year,
            LocalDateTime.now().month,
            LocalDateTime.now().dayOfMonth,
            0,
            0,
            0
        ))

        val millisecondsEndOfToday = localDateTimeToMilliseconds(LocalDateTime.of(
            LocalDateTime.now().year,
            LocalDateTime.now().month,
            LocalDateTime.now().dayOfMonth,
            23,
            59,
            59
        ))

        val selectionArgs = arrayOf(millisecondsStartOfToday.toString(), millisecondsEndOfToday.toString())

        val cursor: Cursor? = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null)

        cursor?.let {
            if (it.moveToFirst()) {
                val totalCalories = it.getInt(0)
                it.close()
                return totalCalories
            }
            it.close()
        }

        return 0 // Return 0 if no calories are consumed today or an error occurs
    }

    fun getLastFoodIntakeInMilliSecondsSinceEpochs(): Long {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_DATE_TAKEN)

        val cursor: Cursor? = db.query(FoodDatabaseHelper.TABLE_NAME, columns, null, null, null, null, "${FoodDatabaseHelper.COLUMN_DATE_TAKEN} DESC")

        cursor?.let {
            if (it.moveToFirst()) {
                val dateTakenIndex = it.getColumnIndex(FoodDatabaseHelper.COLUMN_DATE_TAKEN)

                val dateTaken = if (dateTakenIndex != -1) it.getLong(dateTakenIndex) else -1

                it.close()
                return dateTaken
            }
            it.close()
        }
        return 0 // Return 0 if no food intake is found for today
    }
}

class WaterDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "water_database"
        private const val TABLE_NAME = "water_items"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DATE_TAKEN = "date_taken"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_DATE_TAKEN INTEGER)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // This function inserts a WaterItem into the database with the date time when it was added.
    fun addWaterIntakeForToday(): Int {
        val contentValues = ContentValues()
        AppData.lastTimeWaterAdded = System.currentTimeMillis()
        // set the datetime stamp when the water intake was added
        contentValues.put(COLUMN_DATE_TAKEN, System.currentTimeMillis())
        val db = this.writableDatabase
        return db.insert(TABLE_NAME, null, contentValues).toInt()
    }

    fun getLastWaterIntakeInMilliSecondsSinceEpochs(): Long {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_ID, COLUMN_DATE_TAKEN)

        val cursor: Cursor? = db.query(TABLE_NAME, columns, null, null, null, null, "$COLUMN_DATE_TAKEN DESC")

        cursor?.let {
            if (it.moveToFirst()) {
                val dateTakenIndex = it.getColumnIndex(COLUMN_DATE_TAKEN)

                val dateTaken = if (dateTakenIndex != -1) it.getLong(dateTakenIndex) else -1

                it.close()
                return dateTaken
            }
            it.close()
        }
        return 0 // Return 0 if no water intake is found for today or an error occurs
    }

    fun removeLatestWaterIntakeForToday(): Int {
        val db = this.writableDatabase
        val columns = arrayOf(COLUMN_ID, COLUMN_DATE_TAKEN)

        val selection = "$COLUMN_DATE_TAKEN between ? and ?"
        // get the start and end of today in milliseconds using system time
        val millisecondsStartOfToday = localDateTimeToMilliseconds(LocalDateTime.of(
            LocalDateTime.now().year,
            LocalDateTime.now().month,
            LocalDateTime.now().dayOfMonth,
            0,
            0,
            0
        ))

        val millisecondsEndOfToday = localDateTimeToMilliseconds(LocalDateTime.of(
            LocalDateTime.now().year,
            LocalDateTime.now().month,
            LocalDateTime.now().dayOfMonth,
            23,
            59,
            59
        ))

        val selectionArgs = arrayOf(millisecondsStartOfToday.toString(), millisecondsEndOfToday.toString())

        val cursor: Cursor? = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, "$COLUMN_DATE_TAKEN DESC")

        cursor?.let {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val dateTakenIndex = it.getColumnIndex(COLUMN_DATE_TAKEN)

                val id = if (idIndex != -1) it.getLong(idIndex) else -1
                val dateTaken = if (dateTakenIndex != -1) it.getLong(dateTakenIndex) else -1

                val selection = "$COLUMN_ID = ?"
                val selectionArgs = arrayOf(id.toString())

                val deletedRows = db.delete(TABLE_NAME, selection, selectionArgs)
                it.close()
                return deletedRows
            }
            it.close()
        }
        return 0 // Return 0 if no water intake is found for today or an error occurs
    }

    fun localDateTimeToMilliseconds(localDateTime: LocalDateTime): Long {
        val zoneId = ZoneId.systemDefault() // You can use a specific ZoneId if needed
        val zonedDateTime = localDateTime.atZone(zoneId)
        val instant = zonedDateTime.toInstant()
        return instant.toEpochMilli()
    }

    fun getTotalWaterIntakeForToday(): Int {
        val db = this.readableDatabase
        val columns = arrayOf("COUNT($COLUMN_ID)")

        val selection = "$COLUMN_DATE_TAKEN between ? and ?"
        // get the start and end of today in milliseconds using system time
        val millisecondsStartOfToday = localDateTimeToMilliseconds(LocalDateTime.of(
            LocalDateTime.now().year,
            LocalDateTime.now().month,
            LocalDateTime.now().dayOfMonth,
            0,
            0,
            0
        ))

        val millisecondsEndOfToday = localDateTimeToMilliseconds(LocalDateTime.of(
            LocalDateTime.now().year,
            LocalDateTime.now().month,
            LocalDateTime.now().dayOfMonth,
            23,
            59,
            59
        ))



        val selectionArgs = arrayOf(millisecondsStartOfToday.toString(), millisecondsEndOfToday.toString())

        val cursor: Cursor? = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null)

        cursor?.let {
            if (it.moveToFirst()) {
                val totalWaterIntakeForToday = it.getInt(0)
                it.close()
                return totalWaterIntakeForToday
            }
            it.close()
        }
        return 0 // Return 0 if no calories are consumed today or an error occurs
    }
}
