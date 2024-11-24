import android.content.Context
import androidx.room.Room
import com.example.cna.datos.NoteDao
import com.example.cna.datos.di.NoteDatabase
import com.example.cna.domain.NoteRepository
import com.example.cna.domain.NoteRepositoryImpl
import com.example.cna.domain.TareaRepository
import com.example.cna.domain.TareaRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            NoteDatabase.name
        ).build()

    @Provides
    @Singleton
    fun provideNoteRepository(database: NoteDatabase): NoteRepository =
        NoteRepositoryImpl(dao = database.noteDao)
    @Provides
    @Singleton
    fun provideTaskRepository(database: NoteDatabase): TareaRepository =
        TareaRepositoryImpl(dao = database.taskDao)

}
