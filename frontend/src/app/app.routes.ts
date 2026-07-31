import { Routes } from '@angular/router';
import { AlunosPageComponent } from './pages/alunos/alunos-page.component';
import { CursosPageComponent } from './pages/cursos/cursos-page.component';
import { DisciplinasPageComponent } from './pages/disciplinas/disciplinas-page.component';
import { TurmasPageComponent } from './pages/turmas/turmas-page.component';
import { MatriculasPageComponent } from './pages/matriculas/matriculas-page.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'matriculas' },
  { path: 'alunos', component: AlunosPageComponent },
  { path: 'cursos', component: CursosPageComponent },
  { path: 'disciplinas', component: DisciplinasPageComponent },
  { path: 'turmas', component: TurmasPageComponent },
  { path: 'matriculas', component: MatriculasPageComponent },
  { path: '**', redirectTo: 'matriculas' }
];
