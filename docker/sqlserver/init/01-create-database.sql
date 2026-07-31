-- Database exclusivo da aplicação de matrículas (desafio)
IF DB_ID(N'matriculas_db') IS NULL
BEGIN
    CREATE DATABASE [matriculas_db];
END
GO

ALTER DATABASE [matriculas_db] SET RECOVERY SIMPLE;
GO
