<img width="900" height="2050" alt="9aa268ab-2a77-4a65-a7c8-1f0ad5d24259" src="https://github.com/user-attachments/assets/467155a3-dd30-4ab3-95d9-2d9cbc0f2a70" />

1. Rotation : L’Activity est recréée, mais le ViewModel est conservé par Android (ViewModelStore). Il garde son _uiState rempli, et l’UI se ré-abonne à la dernière valeur sans nouvel appel réseau.


2._uiState privé / uiState public : Seul le ViewModel doit modifier l’état. L’UI ne peut que le lire et envoyer des actions (loadPatients(), addPatient()). Cela garantit une seule source de vérité et un flux de données unidirectionnel.


3. État ou événement :
StateFlow = ce qui est affiché maintenant, avec toujours une valeur courante. Exemple : uiState (Loading / Success / Error).
SharedFlow = un fait ponctuel, sans valeur retenue. Exemple : messages (« Patient supprimé »), qui ne doit pas réapparaître après une rotation.



4. val age: Int sans âge dans l’API : Une MissingFieldException est levée (sous-classe de SerializationException). safeCall renvoie « Réponse inattendue du serveur », et toute la liste échoue. Solution : val age: Int? = null.



5. HttpException ou IOException :

HttpException : le serveur répond avec une erreur 4xx ou 5xx. Un patient inexistant donne une 404.
IOException : aucune réponse (réseau coupé, timeout). Un serveur arrêté donne ce cas.


6. Room à la place de l’API :

À modifier : Patient (@Entity), un nouveau PatientDao et AppDatabase, PatientRepository (il appelle le DAO, donc safeCall change), AppContainer. PatientApi, ApiError et Retrofit disparaissent.
Inchangés : PatientViewModel, PatientsUiState, PatientsScreen et MainActivity, tant que le repository garde les mêmes signatures.
