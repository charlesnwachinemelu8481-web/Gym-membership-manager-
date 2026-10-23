package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Database(
    entities = [
        MemberEntity::class,
        PlanEntity::class,
        PaymentEntity::class,
        CheckInEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FitAdminDatabase : RoomDatabase() {
    abstract fun gymDao(): GymDao

    companion object {
        @Volatile
        private var INSTANCE: FitAdminDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FitAdminDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitAdminDatabase::class.java,
                    "fitadmin_database"
                )
                    .addCallback(FitAdminDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class FitAdminDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.gymDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: GymDao) {
            val now = System.currentTimeMillis()
            val oneDay = TimeUnit.DAYS.toMillis(1)

            // Seed Plans
            val plans = listOf(
                PlanEntity(
                    id = 1,
                    name = "Basic Strength",
                    durationDays = 30,
                    durationLabel = "1 Month",
                    price = 45.00,
                    description = "Cardio suite & free weights access",
                    perks = "Gym Floor, Standard Locker Room",
                    colorHex = "#64748B",
                    isPopular = false
                ),
                PlanEntity(
                    id = 2,
                    name = "Pro Fitness & Classes",
                    durationDays = 30,
                    durationLabel = "1 Month",
                    price = 75.00,
                    description = "Full gym + all group fitness classes",
                    perks = "Gym Floor, Group Classes, Sauna & Steam, Free WiFi",
                    colorHex = "#84CC16",
                    isPopular = true
                ),
                PlanEntity(
                    id = 3,
                    name = "Quarterly Beast",
                    durationDays = 90,
                    durationLabel = "3 Months",
                    price = 195.00,
                    description = "3 months all-inclusive access with guest privileges",
                    perks = "Full Gym, Sauna, 2 Monthly Guest Passes, 1 PT Assessment",
                    colorHex = "#F59E0B",
                    isPopular = false
                ),
                PlanEntity(
                    id = 4,
                    name = "VIP All-Access + PT",
                    durationDays = 180,
                    durationLabel = "6 Months",
                    price = 440.00,
                    description = "Premium tier with dedicated trainer & recovery lounge",
                    perks = "24/7 Access, 2x Monthly 1-on-1 PT, HydroMassage, Locker Rental",
                    colorHex = "#111827",
                    isPopular = false
                ),
                PlanEntity(
                    id = 5,
                    name = "Annual Champion",
                    durationDays = 365,
                    durationLabel = "1 Year",
                    price = 750.00,
                    description = "Ultimate fitness freedom for 365 days",
                    perks = "All Facilities, Priority Class Booking, Nutrition Coach, Merch Pack",
                    colorHex = "#84CC16",
                    isPopular = false
                )
            )
            dao.insertAllPlans(plans)

            // Seed Members
            val members = listOf(
                MemberEntity(
                    id = 1,
                    memberCode = "FA-1001",
                    fullName = "Marcus Vance",
                    phone = "+1 (555) 234-5678",
                    email = "marcus.v@example.com",
                    avatarColorIndex = 0,
                    planId = 2,
                    planName = "Pro Fitness & Classes",
                    startDate = now - 45 * oneDay,
                    expiryDate = now + 45 * oneDay,
                    assignedTrainer = "Devon Cole",
                    emergencyContact = "Sarah Vance: +1 (555) 234-9999"
                ),
                MemberEntity(
                    id = 2,
                    memberCode = "FA-1002",
                    fullName = "Elena Rostova",
                    phone = "+1 (555) 345-6789",
                    email = "elena.fit@example.com",
                    avatarColorIndex = 1,
                    planId = 4,
                    planName = "VIP All-Access + PT",
                    startDate = now - 60 * oneDay,
                    expiryDate = now + 120 * oneDay,
                    assignedTrainer = "Marcus Vance",
                    emergencyContact = "Dmitri R.: +1 (555) 345-0011"
                ),
                MemberEntity(
                    id = 3,
                    memberCode = "FA-1003",
                    fullName = "David Chen",
                    phone = "+1 (555) 456-7890",
                    email = "david.chen@example.com",
                    avatarColorIndex = 2,
                    planId = 1,
                    planName = "Basic Strength",
                    startDate = now - 28 * oneDay,
                    expiryDate = now + 2 * oneDay, // Expiring in 2 days!
                    assignedTrainer = "None",
                    emergencyContact = "Lily Chen: +1 (555) 456-1122"
                ),
                MemberEntity(
                    id = 4,
                    memberCode = "FA-1004",
                    fullName = "Sophia Williams",
                    phone = "+1 (555) 567-8901",
                    email = "sophia.w@example.com",
                    avatarColorIndex = 3,
                    planId = 2,
                    planName = "Pro Fitness & Classes",
                    startDate = now - 25 * oneDay,
                    expiryDate = now + 5 * oneDay, // Expiring in 5 days!
                    assignedTrainer = "Elena Rostova",
                    emergencyContact = "James W.: +1 (555) 567-2233"
                ),
                MemberEntity(
                    id = 5,
                    memberCode = "FA-1005",
                    fullName = "Tyler Brooks",
                    phone = "+1 (555) 678-9012",
                    email = "tbrooks@example.com",
                    avatarColorIndex = 4,
                    planId = 1,
                    planName = "Basic Strength",
                    startDate = now - 35 * oneDay,
                    expiryDate = now - 5 * oneDay, // Expired 5 days ago!
                    assignedTrainer = "None",
                    emergencyContact = "Kelly Brooks: +1 (555) 678-3344"
                ),
                MemberEntity(
                    id = 6,
                    memberCode = "FA-1006",
                    fullName = "Amina Diallo",
                    phone = "+1 (555) 789-0123",
                    email = "amina.d@example.com",
                    avatarColorIndex = 5,
                    planId = 3,
                    planName = "Quarterly Beast",
                    startDate = now - 15 * oneDay,
                    expiryDate = now + 75 * oneDay,
                    assignedTrainer = "Devon Cole",
                    emergencyContact = "Ibrahim D.: +1 (555) 789-4455"
                ),
                MemberEntity(
                    id = 7,
                    memberCode = "FA-1007",
                    fullName = "Lucas Thorne",
                    phone = "+1 (555) 890-1234",
                    email = "lucas.thorne@example.com",
                    avatarColorIndex = 6,
                    planId = 2,
                    planName = "Pro Fitness & Classes",
                    startDate = now - 33 * oneDay,
                    expiryDate = now - 3 * oneDay, // Expired 3 days ago!
                    assignedTrainer = "Marcus Vance",
                    emergencyContact = "Anna Thorne: +1 (555) 890-5566"
                ),
                MemberEntity(
                    id = 8,
                    memberCode = "FA-1008",
                    fullName = "Chloe Morales",
                    phone = "+1 (555) 901-2345",
                    email = "chloe.m@example.com",
                    avatarColorIndex = 7,
                    planId = 5,
                    planName = "Annual Champion",
                    startDate = now - 90 * oneDay,
                    expiryDate = now + 275 * oneDay,
                    assignedTrainer = "Devon Cole",
                    emergencyContact = "Carlos M.: +1 (555) 901-6677"
                ),
                MemberEntity(
                    id = 9,
                    memberCode = "FA-1009",
                    fullName = "Jackson Reed",
                    phone = "+1 (555) 112-3344",
                    email = "jreed@example.com",
                    avatarColorIndex = 8,
                    planId = 2,
                    planName = "Pro Fitness & Classes",
                    startDate = now - 26 * oneDay,
                    expiryDate = now + 4 * oneDay, // Expiring in 4 days!
                    assignedTrainer = "None",
                    emergencyContact = "Paige Reed: +1 (555) 112-7788"
                ),
                MemberEntity(
                    id = 10,
                    memberCode = "FA-1010",
                    fullName = "Zoe Katsaros",
                    phone = "+1 (555) 223-4455",
                    email = "zoe.k@example.com",
                    avatarColorIndex = 9,
                    planId = 3,
                    planName = "Quarterly Beast",
                    startDate = now - 10 * oneDay,
                    expiryDate = now + 80 * oneDay,
                    assignedTrainer = "Elena Rostova",
                    emergencyContact = "Nikos K.: +1 (555) 223-8899"
                )
            )
            dao.insertAllMembers(members)

            // Seed Payments
            val payments = listOf(
                PaymentEntity(
                    id = 1,
                    memberId = 8,
                    memberName = "Chloe Morales",
                    memberCode = "FA-1008",
                    planId = 5,
                    planName = "Annual Champion",
                    amount = 750.00,
                    paymentDate = now - 90 * oneDay,
                    paymentMethod = "Credit Card",
                    transactionRef = "TXN-88410",
                    extendedDays = 365,
                    notes = "Annual upfront payment"
                ),
                PaymentEntity(
                    id = 2,
                    memberId = 2,
                    memberName = "Elena Rostova",
                    memberCode = "FA-1002",
                    planId = 4,
                    planName = "VIP All-Access + PT",
                    amount = 440.00,
                    paymentDate = now - 60 * oneDay,
                    paymentMethod = "Debit Card",
                    transactionRef = "TXN-77312",
                    extendedDays = 180,
                    notes = "VIP semi-annual bundle"
                ),
                PaymentEntity(
                    id = 3,
                    memberId = 1,
                    memberName = "Marcus Vance",
                    memberCode = "FA-1001",
                    planId = 2,
                    planName = "Pro Fitness & Classes",
                    amount = 75.00,
                    paymentDate = now - 15 * oneDay,
                    paymentMethod = "Credit Card",
                    transactionRef = "TXN-99120",
                    extendedDays = 30,
                    notes = "Monthly auto-renewal"
                ),
                PaymentEntity(
                    id = 4,
                    memberId = 6,
                    memberName = "Amina Diallo",
                    memberCode = "FA-1006",
                    planId = 3,
                    planName = "Quarterly Beast",
                    amount = 195.00,
                    paymentDate = now - 15 * oneDay,
                    paymentMethod = "Mobile Pay",
                    transactionRef = "TXN-99144",
                    extendedDays = 90,
                    notes = "Apple Pay checkout"
                ),
                PaymentEntity(
                    id = 5,
                    memberId = 10,
                    memberName = "Zoe Katsaros",
                    memberCode = "FA-1010",
                    planId = 3,
                    planName = "Quarterly Beast",
                    amount = 195.00,
                    paymentDate = now - 10 * oneDay,
                    paymentMethod = "Cash",
                    transactionRef = "TXN-99201",
                    extendedDays = 90,
                    notes = "Front desk cash received"
                ),
                PaymentEntity(
                    id = 6,
                    memberId = 4,
                    memberName = "Sophia Williams",
                    memberCode = "FA-1004",
                    planId = 2,
                    planName = "Pro Fitness & Classes",
                    amount = 75.00,
                    paymentDate = now - 25 * oneDay,
                    paymentMethod = "Credit Card",
                    transactionRef = "TXN-98432",
                    extendedDays = 30,
                    notes = "Standard monthly"
                )
            )
            dao.insertAllPayments(payments)

            // Seed Check-Ins
            val checkIns = listOf(
                CheckInEntity(
                    id = 1,
                    memberId = 1,
                    memberName = "Marcus Vance",
                    memberCode = "FA-1001",
                    planName = "Pro Fitness & Classes",
                    timestamp = now - TimeUnit.MINUTES.toMillis(18),
                    status = "SUCCESS",
                    notes = "Leg Day Workout"
                ),
                CheckInEntity(
                    id = 2,
                    memberId = 2,
                    memberName = "Elena Rostova",
                    memberCode = "FA-1002",
                    planName = "VIP All-Access + PT",
                    timestamp = now - TimeUnit.MINUTES.toMillis(42),
                    status = "SUCCESS",
                    notes = "PT Session with Coach"
                ),
                CheckInEntity(
                    id = 3,
                    memberId = 6,
                    memberName = "Amina Diallo",
                    memberCode = "FA-1006",
                    planName = "Quarterly Beast",
                    timestamp = now - TimeUnit.HOURS.toMillis(2),
                    status = "SUCCESS",
                    notes = "Spin Cycle Class"
                ),
                CheckInEntity(
                    id = 4,
                    memberId = 10,
                    memberName = "Zoe Katsaros",
                    memberCode = "FA-1010",
                    planName = "Quarterly Beast",
                    timestamp = now - TimeUnit.HOURS.toMillis(3),
                    status = "SUCCESS",
                    notes = "Cardio & Stretching"
                ),
                CheckInEntity(
                    id = 5,
                    memberId = 8,
                    memberName = "Chloe Morales",
                    memberCode = "FA-1008",
                    planName = "Annual Champion",
                    timestamp = now - TimeUnit.HOURS.toMillis(4),
                    status = "SUCCESS",
                    notes = "Morning Weights Routine"
                )
            )
            dao.insertAllCheckIns(checkIns)
        }
    }
}
