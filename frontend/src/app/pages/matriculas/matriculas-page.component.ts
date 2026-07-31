import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { Aluno, Matricula, Turma } from '../../core/models';
import { AlunoService } from '../../services/aluno.service';
import { TurmaService } from '../../services/turma.service';
import { MatriculaService } from '../../services/matricula.service';
import { NotificationService } from '../../core/notification.service';

@Component({
  selector: 'app-matriculas-page',
  standalone: true,
  imports: [ReactiveFormsModule, NgFor, NgIf],
  templateUrl: './matriculas-page.component.html',
  styleUrl: './matriculas-page.component.css'
})
export class MatriculasPageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly matriculaService = inject(MatriculaService);
  private readonly alunoService = inject(AlunoService);
  private readonly turmaService = inject(TurmaService);
  private readonly notify = inject(NotificationService);

  alunos: Aluno[] = [];
  turmas: Turma[] = [];
  matriculas: Matricula[] = [];

  form = this.fb.nonNullable.group({
    alunoId: [0, [Validators.required, Validators.min(1)]],
    turmaId: [0, [Validators.required, Validators.min(1)]]
  });

  filtro = this.fb.nonNullable.group({
    alunoId: [0],
    turmaId: [0]
  });

  ngOnInit(): void {
    this.alunoService.listar().subscribe(lista => {
      this.alunos = lista;
      if (lista.length) {
        this.form.patchValue({ alunoId: lista[0].id });
        this.filtro.patchValue({ alunoId: lista[0].id });
        this.consultar();
      }
    });
    this.turmaService.listar().subscribe(lista => {
      this.turmas = lista;
      if (lista.length) {
        this.form.patchValue({ turmaId: lista[0].id });
      }
    });
  }

  matricular(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { alunoId, turmaId } = this.form.getRawValue();
    this.matriculaService.matricular(alunoId, turmaId).subscribe(() => {
      this.notify.sucesso('Matricula criada (PENDENTE)');
      this.filtro.patchValue({ alunoId, turmaId: 0 });
      this.consultar();
      this.turmaService.listar().subscribe(lista => (this.turmas = lista));
    });
  }

  consultar(): void {
    const { alunoId, turmaId } = this.filtro.getRawValue();
    if (!alunoId && !turmaId) {
      this.notify.erro('Informe aluno ou turma para consultar');
      return;
    }
    this.matriculaService
      .consultar({
        alunoId: alunoId || undefined,
        turmaId: turmaId || undefined
      })
      .subscribe(lista => (this.matriculas = lista));
  }

  confirmar(m: Matricula): void {
    this.matriculaService.confirmar(m.id).subscribe(() => {
      this.notify.sucesso('Matricula confirmada (vaga consumida)');
      this.consultar();
      this.turmaService.listar().subscribe(lista => (this.turmas = lista));
    });
  }

  cancelar(m: Matricula): void {
    this.matriculaService.cancelar(m.id).subscribe(() => {
      this.notify.sucesso('Matricula cancelada');
      this.consultar();
      this.turmaService.listar().subscribe(lista => (this.turmas = lista));
    });
  }
}
