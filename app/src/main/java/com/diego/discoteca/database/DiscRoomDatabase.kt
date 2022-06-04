package com.diego.discoteca.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.diego.discoteca.util.DATABASE_NAME

@Database(
    entities = [DatabaseDisc::class],
    version = 1,
    exportSchema = false
)
abstract class DiscRoomDatabase : RoomDatabase() {

    abstract val discDatabaseDao: DiscDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: DiscRoomDatabase? = null

        fun getInstance(context: Context): DiscRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiscRoomDatabase::class.java,
                    DATABASE_NAME
                )
                  //  .addCallback(DiscDatabaseCallback(CoroutineScope(SupervisorJob())))
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

   /* private class DiscDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    //val discDao = database.discDatabaseDao
                    val repo = MyApp.instance.repository
                    repo.deleteAllDiscs()
                  //  val repo = MyApp.instance.repository

                    *//*val disc = Disc(

                    )*//*

                    var disc = Disc(
                        name = "Kenny Arkana",
                        title = "Bonjour",
                        year = "1999",
                        addBy = MANUALLY
                    )
                    repo.insert(disc)

                    disc = Disc(
                        name = "TA GUEULE",
                        title = "Bonjour",
                        year = "1999",
                        addBy = MANUALLY
                    )
                    repo.insert(disc)


                    *//*val disc = Disc(
                        name = "Caribbean Dandee",
                        title = "Caribbean Dandee",
                        year = 2015,
                        country = "France",
                        format = "1 Media
                        2022-05-16 19:10:05.169 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album , Digipakzz formatMedia=CD, coverImage=https://i.discogs.com/Kb11A2mQWRLDsaV6sfxBL-1kon5e9xovSZ-XPVomC_8/rs:fit/g:sm/q:90/h:534/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTc4MTY2/NjctMTUwNzQyODUz/Mi0xOTQ4LmpwZWc.jpeg",
                        formatMedia = formatMedia,
                        coverImage = result.coverImage ?: "",
                        barcode = barcode,
                        idDisc = result.id!!,
                        addBy = SCAN
                    )*//*

                    *//*val list = listOf<Disc>(Disc(id=394, name=Caribbean Dandee, title=Caribbean Dandee, year=2015, country=France, format=1 Media
                            2022-05-16 19:10:05.169 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album , Digipak, formatMedia=CD, coverImage=https://i.discogs.com/Kb11A2mQWRLDsaV6sfxBL-1kon5e9xovSZ-XPVomC_8/rs:fit/g:sm/q:90/h:534/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTc4MTY2/NjctMTUwNzQyODUz/Mi0xOTQ4LmpwZWc.jpeg, barcode=3298498335118, idDisc=7816667, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=411, name=Cypress Hill, title=Los Grandes Éxitos En Español, year=1999, country=Europe, format=1 Media
                    2022-05-16 19:10:05.169 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album | Compilation, formatMedia=CD, coverImage=https://i.discogs.com/RyhOgp9PFtcq2QGRsZNhI6EXphooH-NHVT0ABp5nVlI/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE3MDIy/NS0xMjY2MzM1MzE3/LmpwZWc.jpeg, barcode=5099749628724, idDisc=170225, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=340, name=DJ James, title=Opération Rap, year=2002, country=France, format=1 Media
                    2022-05-16 19:10:05.169 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Compilation | Mixed, formatMedia=CD, coverImage=https://i.discogs.com/NttJ56OfHGuoF8L--lpVmY1bdicdxCKpydZcyXmjRDo/rs:fit/g:sm/q:90/h:596/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTM0ODM4/NDQtMTUxMTYxODQ0/My0zMTkyLmpwZWc.jpeg, barcode=5050466089823, idDisc=3483844, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=346, name=DJ Tal, title=Plaidoiries, year=1998, country=France, format=1 Media
                    2022-05-16 19:10:05.169 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Compilation, formatMedia=CD, coverImage=https://i.discogs.com/G5kxjM1T9R31NRUGXZLMPnrBP0hbfl6tyD8yC1Rr-3k/rs:fit/g:sm/q:90/h:481/w:548/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE5MjQ5/MzYtMTI1MjgyMTEz/OC5qcGVn.jpeg, barcode=3306640487421, idDisc=1924936, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=406, name=Daddy Lord C, title=Le Noble Art, year=1998, country=France, format=1 Media
                    2022-05-16 19:10:05.169 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/EYmZjMGTu0Ce1q9gDowMtNjAFY5PVJ4bqlLCeqxx_ZE/rs:fit/g:sm/q:90/h:598/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTk1MzUw/OC0xNTA1NTQwOTA4/LTI3NDUuanBlZw.jpeg, barcode=3229261221420, idDisc=953508, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=407, name=Daddy Lord C & La Cliqua, title=Freaky Flow Remix, year=1995, country=France, format=1 Media
                    2022-05-16 19:10:05.169 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Maxi-Single, formatMedia=CD, coverImage=https://i.discogs.com/-f6TDxgHkq6e_nkHKmnWCR3ng3OHO7_fKjXy35jYoho/rs:fit/g:sm/q:90/h:599/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTkxNTU3/MC0xNTMzMzM0Mzc4/LTc4MTguanBlZw.jpeg, barcode=3448963601129, idDisc=915570, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=404, name=Dee Nasty, title=Le Diamant Est Eternel, year=1998, country=France, format=1 Media
                    2022-05-16 19:10:05.169 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Mixed, formatMedia=CD, coverImage=https://i.discogs.com/Bd5SbkLZlBheeafU3IHRbLlmgBAgpPAhJMLs1uk6Vt4/rs:fit/g:sm/q:90/h:596/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTc0NTgx/MC0xNTEwNzQ0NzQ1/LTg5OTguanBlZw.jpeg, barcode=3512842488023, idDisc=745810, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=392, name=Doc Gynéco, title=Première Consultation, year=1997, country=France, format=1 Media
                    2022-05-16 19:10:05.169 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album | Reissue, formatMedia=CD, coverImage=https://i.discogs.com/FtJVqTOdNM6WdB36DXWWi8i9HslDJYFh1y3pyOSxcJY/rs:fit/g:sm/q:90/h:587/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTM0NzM0/Ny0xNDI4NTc3Mzc1/LTcyNTYuanBlZw.jpeg, barcode=724384508425, idDisc=347347, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=403, name=Doc Gynéco, title=Liaisons Dangereuses, year=1998, country=France, format=1 Media
                    2022-05-16 19:10:05.169 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album | Compilation, formatMedia=CD, coverImage=https://i.discogs.com/hIjyImqawDS3ncXXYbpd9EEZj85ihhE8UQEfOba3PCM/rs:fit/g:sm/q:90/h:589/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTEyOTg3/NTAtMTU3ODM5OTg0/OC04NDY0LmpwZWc.jpeg, barcode=724384662622, idDisc=1298750, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=402, name=Don Choa, title=Vapeurs Toxiques, year=2002, country=France, format=2 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: 2 x CD : Album | Enhanced, formatMedia=CD, coverImage=https://i.discogs.com/UpDc5EhmkWcnJfntqt7DDAJMV63BcIArgel80c5k1-0/rs:fit/g:sm/q:90/h:605/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTIyMDE3/MDQtMTYyMjE5MjY3/Ni0xNjc2LmpwZWc.jpeg, barcode=5099750980293, idDisc=2201704, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=401, name=Dontcha, title=État Brut, year=2006, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/6VobSGnw72v98hz-wd5wAMi-3ESatxJv2m0DscKKx1c/rs:fit/g:sm/q:90/h:590/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTgyNTY5/Ni0xNTA1NTM5OTA1/LTU4NDkuanBlZw.jpeg, barcode=3700187622855, idDisc=825696, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=338, name=Gang Starr, title=Full Clip: A Decade Of Gang Starr, year=1999, country=Europe, format=2 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: 2 x CD : Compilation, formatMedia=CD, coverImage=https://i.discogs.com/ggF9WY6MjVGniXQ9DFukLXYVK6N3S3QsSPiLbF1V3PU/rs:fit/g:sm/q:90/h:471/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTM1NDEz/ODUtMTMzNDUxNTM5/NC5qcGVn.jpeg, barcode=724352118922, idDisc=3541385, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=373, name=House Of Pain, title=Same As It Ever Was, year=1994, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: Cassette : Album, formatMedia=Cassette, coverImage=https://i.discogs.com/V9dCGC2MwA9EQ1bdyOlULZ_tj8W-sBCqUTI_TnqZO6A/rs:fit/g:sm/q:90/h:300/w:242/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTY2Nzg1/MjctMTQyNDQ1NDg2/MS00MDE5LmpwZWc.jpeg, barcode=724383980147, idDisc=6678527, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=370, name=IAM, title=20, year=2008, country=France, format=2 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: DVD : DVD-Video
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD, formatMedia=DVD CD, coverImage=https://i.discogs.com/9p6nh2zQ5EEVq_5q0fsf_osiDu2fSL-ibp3irdpnvSk/rs:fit/g:sm/q:90/h:903/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTU0NTg1/NzktMTU1MTAxNTUw/MC04NzkwLmpwZWc.jpeg, barcode=600753083239, idDisc=5458579, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=399, name=IV My People, title=Mission, year=2005, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/zj8Mw5xkE9ifgjExjtTbwXt3dE9Z4tX0bLLgsNizoyg/rs:fit/g:sm/q:90/h:601/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE0ODcz/MjctMTU1NjQ5MzA1/NS0zNTgxLmpwZWc.jpeg, barcode=3760042223221, idDisc=1487327, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=397, name=IV My People / Madizm & Sec.Undo, title=Streetly Street Vol 2, year=2003, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/afdIJqmS-LXFoEFwomAmRr9nGd_qv6LpLulkr14fJMk/rs:fit/g:sm/q:90/h:596/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTM1ODM0/MDMtMTUxMDc0NTE3/MS0yMzYyLmpwZWc.jpeg, barcode=3760042221722, idDisc=3583403, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=368, name=Jay-Z, title=The Blueprint 3, year=2009, country=US, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/p4oE_jlT50PtCJuwP93ty7TuB1pqI0uqwAw7LlIccZg/rs:fit/g:sm/q:90/h:586/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE5MTE3/NjAtMTQ5NjcxMDI2/OC02NzM2LmpwZWc.jpeg, barcode=075678958663, idDisc=1911760, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=395, name=Joey Starr, title=Gare Au Jaguarr, year=2006, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/YPXXJvcURWCy3f1Um_Kvidf8YHajgBYLmgs2V7DhBBo/rs:fit/g:sm/q:90/h:601/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTM0MDUz/MTgtMTUxNDMwMjY0/OS0xMDU0LmpwZWc.jpeg, barcode=886970009522, idDisc=3405318, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=393, name=KDD, title=Resurrection, year=1998, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/e0D22SoifAJ5uGM6SjzbXVMb9AU-mdpFfPVzCRUMEKY/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTIyNTE5/MzctMTQyODczNDM0/OS0zMjYwLmpwZWc.jpeg, barcode=5099748963529, idDisc=2251937, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=366, name=Kabal, title=États D'Âmes, year=1998, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/8q8lzDtNLh95JadraxVspYtBfE1VrdsHN7Rnuke-biU/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTExNTE5/OTUtMTE5NjQxOTc4/MC5qcGVn.jpeg, barcode=3356575705077, idDisc=1151995, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=360, name=Keny Arkana, title=L'Esquisse 2, year=2011, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album | Mixtape, formatMedia=CD, coverImage=https://i.discogs.com/X2-Et-yWAqCn2b-e-RmawlwPqjhGnxdxGXQhK52gEdM/rs:fit/g:sm/q:90/h:598/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTI5NDEx/MDUtMTU1NTA0NTIz/MC0zNDQ5LmpwZWc.jpeg, barcode=5060107728257, idDisc=2941105, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=408, name=Kery James, title=Si C'Était À Refaire, year=2001, country=France, format=2 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: 2 x CD : Album | Limited Edition, formatMedia=CD, coverImage=https://i.discogs.com/zxa_RXR4GurWMzCdkr49cnVJlIF5L5JHNCvo_U11Tgc/rs:fit/g:sm/q:90/h:336/w:344/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTMwODI5/MDEtMTMxNDg5NjI4/Mi5qcGVn.jpeg, barcode=5050466019929, idDisc=3082901, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=350, name=Kool Shen, title=Dernier Round Au Zénith , year=2005, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: Hybrid : DVDplus | Album, formatMedia=Hybrid, coverImage=https://i.discogs.com/peGv59Pwtv8tQuJbYDVClTolx0ag4VINV2FJqeJll3M/rs:fit/g:sm/q:90/h:535/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTExNzIy/NTczLTE2MzcxNTg1/NTgtOTExMC5qcGVn.jpeg, barcode=3760042223122, idDisc=11722573, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=369, name=Les Sages Poetes De La Rue, title=Qu'Est-Ce Qui Fait Marcher Les Sages, year=2010, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album | Reissue, formatMedia=CD, coverImage=https://i.discogs.com/jPS1RTtcAIEodHi0B-CMLgEcO8AApZjasgkRsIkwAuU/rs:fit/g:sm/q:90/h:588/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTU5MzIy/MTAtMTYyMzc4MzY5/OS05MDgyLmpwZWc.jpeg, barcode=3700187639020, idDisc=5932210, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=358, name=Mafia Trece, title=L'Envers Du Décor, year=1999, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/HQf9dQIPqo4c8zm_PA0gCj1HDEnEoUVo5n8R0P912Dk/rs:fit/g:sm/q:90/h:297/w:300/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTEzNDc2/MDAtMTI4NzgzNDkx/NS5qcGVn.jpeg, barcode=5099749618428, idDisc=1347600, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=367, name=Mr. Oizo, title=Flat Beat, year=1999, country=UK & Europe, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Single , J-card case, formatMedia=CD, coverImage=https://i.discogs.com/X7Bfg3HCfoD4VMYaB5jriEA0DVzaUoRCKOUdfQGkwq0/rs:fit/g:sm/q:90/h:530/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE2MDEy/Mi0xNjE4NzUyNzAz/LTU0ODguanBlZw.jpeg, barcode=5413356711226, idDisc=160122, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=337, name=Nessbeal, title=Sélection Naturelle, year=2011, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/OWh6s51ukLUVsOkJvWypoOgd9XJOuMFmlU0kxP5ACEc/rs:fit/g:sm/q:90/h:400/w:400/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTM4Mzcw/OTctMTM0NjgzOTQw/Mi03OTQ3LmpwZWc.jpeg, barcode=886919033120, idDisc=3837097, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=421, name=Nirvana, title=Nevermind, year=1991, country=Europe, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/FIFfbJ_z8bmZelp2pgsGGbsiszRck9ovxZjb4Zd21AQ/rs:fit/g:sm/q:90/h:599/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTg1NDY4/NjQtMTQ2Mzc5MDYy/My03MTEyLmpwZWc.jpeg, barcode=, idDisc=8546864, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=3, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=344, name=One Shot, title=Taxi 2, year=2000, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Compilation, formatMedia=CD, coverImage=https://i.discogs.com/e2t4N1Tcz52MBXHZLngJI1UTY4SFIDvLBKo6y7vJKv0/rs:fit/g:sm/q:90/h:532/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE0Mjc1/OTE0LTE1NzEyNTcy/NzItODMzNS5qcGVn.jpeg, barcode=724384899820, idDisc=14275914, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=374, name=Orelsan, title=Perdu D'Avance, year=2009, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/DTSr0-SNovKhNgMOmuJiCxj5t3xR3RlBTerXxLyUw-A/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE3ODk3/NjktMTYzOTc3NDEw/OC03MDgxLmpwZWc.jpeg, barcode=3596971402723, idDisc=1789769, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=348, name=OutKast, title=Idlewild, year=2006, country=US, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/kmE6I004P9Xb54jQOzubXWBgH7nz9nNnWqTJ5XOfLIE/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTg2NjUy/NTQtMTQ2NjE4ODg3/NC01ODE0LmpwZWc.jpeg, barcode=828767579122, idDisc=8665254, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=371, name=Oxmo Puccino, title=Opéra Puccino, year=1998, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/eTMFWpqNCanINQ428yW9GYIB999YWnKWt5lJEj-fX88/rs:fit/g:sm/q:90/h:594/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTY4Mzgz/MC0xNTEwNTczNzgz/LTUxNDcuanBlZw.jpeg, barcode=724384578626, idDisc=683830, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=372, name=Oxmo Puccino, title=La Voix Lactée , year=2015, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album , Book album, formatMedia=CD, coverImage=https://i.discogs.com/WAZy3oe6ahRPtEXtVDX3-K9BiHD-8KNmkdNk-sr19XQ/rs:fit/g:sm/q:90/h:512/w:512/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTgwMjc2/NjItMTQ1Mzc0MDY1/Ni0zNjMwLmpwZWc.jpeg, barcode=3596973286420, idDisc=8027662, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=383, name=Oxmo Puccino, title=Le Cactus De Sibérie, year=2004, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/d1jb7bgRpmGsmJD0r29HhAD0nRH-AZne8GlxHqTvpp0/rs:fit/g:sm/q:90/h:165/w:165/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTI5NTA2/MjAtMTMwODc2MzE5/Ny5qcGVn.jpeg, barcode=724359715629, idDisc=2950620, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=365, name=Passi, title=Les Tentations (Édition Collector 1997-2017), year=2017, country=Europe, format=2 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: 2 x CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/Yw-CSNHSCDXJcr5XtlIqWfwxpT-1MFnGZtL7tVPacnk/rs:fit/g:sm/q:90/h:454/w:448/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTExMTcx/MDU4LTE1MTExNzE1/NTEtNDI0Ny5qcGVn.jpeg, barcode=190295733513, idDisc=11171058, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=342, name=Pit Baccardi, title=Pit Baccardi, year=1999, country=Canada, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/NCWTuhycpkV6cgqYimRNGMGxbUzODiotfoTniHifcu8/rs:fit/g:sm/q:90/h:613/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTEzMDQz/OTM5LTE1NDcwNDU2/NTktNzM5OS5qcGVn.jpeg, barcode=724384793128, idDisc=13043939, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=378, name=Pop Smoke, title=Shoot For The Stars Aim For The Moon, year=2020, country=Europe, format=2 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: 2 x CD : Album | Deluxe Edition, formatMedia=CD, coverImage=https://i.discogs.com/xUcvRdY8qXLU-cbEyfqNVGZBrRDUDLqEY9HMRaIWDFg/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE2OTE0/MjYxLTE2MTA1Njcw/MzEtOTg0Ni5qcGVn.jpeg, barcode=602435269290, idDisc=16914261, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=343, name=Shurik'n, title=Où Je Vis, year=2000, country=Europe, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album | Reissue, formatMedia=CD, coverImage=https://i.discogs.com/F1hVr90QejpUIHvlWZBh1VNTz_rQhXDCx3YJUDWyiZc/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTEzNzA1/NTktMTIxMzY5Njkw/My5qcGVn.jpeg, barcode=724384893729, idDisc=1370559, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=336, name=Sinik, title=Sang Froid, year=2006, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/sBvwZBJYHN0igHIyXerhinzND2m7ZQf6Xr8SaGz3udU/rs:fit/g:sm/q:90/h:420/w:420/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTk1NTcz/OS0xMzAxNDA4MzU1/LmpwZWc.jpeg, barcode=825646284429, idDisc=955739, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=380, name=Sniper, title=Du Rire Aux Larmes / Grave Dans La Roche, year=2006, country=Germany, format=2 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: 2 x CD : Album | Compilation | Stereo, formatMedia=CD, coverImage=https://i.discogs.com/2Aqmvssmqi965DaodLBQgi5QRULagRAtPl7XD5KQcLM/rs:fit/g:sm/q:90/h:587/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTEwNDky/NjAyLTE0OTg1NzMx/NDYtOTE1My5qcGVn.jpeg, barcode=825646349524, idDisc=10492602, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=388, name=Snoop Doggy Dogg*, title=Doggystyle, year=1993, country=Europe, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album | Stereo, formatMedia=CD, coverImage=https://i.discogs.com/l5UG8Z0po0mRk1wfIuM20lQjpYdvf6iy8qYH1t1PCnA/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTU5MzM1/OS0xNjUxNDA4MDA4/LTc1NTUuanBlZw.jpeg, barcode=765449227929, idDisc=593359, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=379, name=Starflam, title=Donne-Moi De L'amour, year=2003, country=Europe, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/iPv-5066Qq6IP00hzsV3WCixXyvgLKRxVHc7Q9A3xhs/rs:fit/g:sm/q:90/h:500/w:500/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTk0MzYz/NS0xMTc1NjMzODU5/LmpwZWc.jpeg, barcode=724359204529, idDisc=943635, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=362, name=Stomy Bugsy, title=Trop Jeune Pour Mourir, year=2000, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/q0G6Ts9Ce-AK4Xy9IkoSndy7iuEnra2e0DB4e8jeLu0/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE5MzUz/NDMtMTMwMTUwMzIz/MS5qcGVn.jpeg, barcode=5099749668621, idDisc=1935343, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=363, name=Stomy Bugsy, title=Trop Jeune Pour Mourir, year=2000, country=France, format=3 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: 3 x Vinyl : LP | Album, formatMedia=Vinyl, coverImage=https://i.discogs.com/2GTTfajAmTv5aIzA-vr5Q-QOX4ZcvsXMaHtAkg31lKc/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTQ5NzE3/MzMtMTQwMTQ1MTIy/NS02NzA2LmpwZWc.jpeg, barcode=5099749668621, idDisc=4971733, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=382, name=Sully Sefil, title=Sullysefilistic, year=2001, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album | Enhanced, formatMedia=CD, coverImage=https://i.discogs.com/Ih-WZz-dZvGqoMV2zwdvK7LtlX8VbfN1UVPAHrATnPk/rs:fit/g:sm/q:90/h:594/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTQ5ODY2/Mi0xNjI3NDg4Nzc4/LTQyMDYuanBlZw.jpeg, barcode=5033197178080, idDisc=498662, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=352, name=Tandem, title=C'est Toujours Pour Ceux Qui Savent, year=2005, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/DNzTVdxhId9EPoKXaNdIi1FKFreRQFjUaI4S0Ji7Tz8/rs:fit/g:sm/q:90/h:530/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTc1NTEw/NS0xNTAzNTA5Mzgy/LTYxMTMuanBlZw.jpeg, barcode=3596971025625, idDisc=755105, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=390, name=The Alchemist*, title=1st Infantry, year=2004, country=Europe, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/Pi158EJ54uvgMYOOfy4GuT84Yjq4WooY_7ddI0GQt7w/rs:fit/g:sm/q:90/h:595/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE2MzIx/MzYtMTU3OTM2OTgw/MC01NTIyLmpwZWc.jpeg, barcode=826601323214, idDisc=1632136, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=384, name=The Notorious B.I.G.*, title=Born Again, year=1999, country=Europe, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/TPeH9OmaO5oNFC3xDFUfBwEKx_5IcVpb7f2QNjGlOz4/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE0NTU3/MDUtMTM1OTU0MjI5/Mi01ODM4LmpwZWc.jpeg, barcode=786127302325, idDisc=1455705, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=375, name=Tout Simplement Noir, title=Dans Paris Nocturne, year=1995, country=France, format=1 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: CD : Album, formatMedia=CD, coverImage=https://i.discogs.com/_uuBRBAdL2T1UTersS0gX_cb38-vzpXoNZ0Y57e1wkM/rs:fit/g:sm/q:90/h:597/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTIyNjE3/NDk3LTE2NDgwNTcy/NTAtMjc1OS5qcGVn.jpeg, barcode=3448966300128, idDisc=22617497, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=)), Disc(id=376, name=Various, title=Rap Performance, year=2005, country=France, format=2 Media
                    2022-05-16 19:10:05.170 11763-11763/com.diego.discoteca E/DatabasePagingSourceAllDiscs3: 2 x CD : Compilation, formatMedia=CD, coverImage=https://i.discogs.com/MXGXSSazpnJQc334YHCHWpoN_e2ln7HfjpeU1WcXp2k/rs:fit/g:sm/q:90/h:360/w:360/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTU5MzIw/MzktMTQwNjY1MjI0/My0xMTEwLmpwZWc.jpeg, barcode=3760107200280, idDisc=5932039, isPresentByManually=false, isPresentByScan=false, isPresentBySearch=false, addBy=2, discLight=DiscLight(id=0, name=, title=, year=, country=, format=, formatMedia=))*//*


                    *//*val repo = MyApp.instance.repository

                    var disc = Disc(
                        name = "Keny Arkana",
                        title = "Bonjour",
                        year = "1999"
                    )

                    repo.insert(disc)

                    disc = Disc(
                        name = "KENY ARKANA",
                        title = "Bonjour",
                        year = "1999"
                    )

                    repo.insert(disc)

                    disc = Disc(
                        name = "keny arkana",
                        title = "Bonjour",
                        year = "1999"
                    )

                    repo.insert(disc)

                    disc = Disc(
                        name = "kény arkana",
                        title = "Bonjour",
                        year = "1999"
                    )

                    repo.insert(disc)

                    disc = Disc(
                        name = "KÉNY arkana",
                        title = "Bonjour",
                        year = "1999"
                    )

                    repo.insert(disc)
*//*

                    *//*discDao.insert(disc)

                    disc = DatabaseDisc(
                        0,
                        "",
                        "A",
                        "b",
                        "1996"
                    )
                    discDao.insert(disc)

                    disc = DatabaseDisc(
                        0,
                        "",
                        "KENY ARKANA",
                        "Bonjour",
                        "1999"
                    )
                    discDao.insert(disc)

                    disc = DatabaseDisc(
                        0,
                        "",
                        "Keny Arkana",
                        "Bonjour",
                        "1999"
                    )
                    discDao.insert(disc)

                    disc = DatabaseDisc(
                        0,
                        "",
                        "Kény Arkana",
                        "Bonjour",
                        "1999"
                    )
                    discDao.insert(disc)

                    disc = DatabaseDisc(
                        0,
                        "",
                        "Wu-Tang Clan",
                        "Enter the Wu-Tang (36 Chambers)",
                        "1999"
                    )
                    discDao.insert(disc)*//*


                }
            }
        }
    }*/
}