import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { Disciplina, Turma } from '../../core/models';
import { DisciplinaService } from '../../services/disciplina.service';
import { TurmaService } from '../../services/turma.service';
import { NotificationService } from '../../core/notification.service';

@Component({
  selector: 'app-turmas-page',
  standalone: true,
  imports: [ReactiveFormsModule, NgFor, NgIf],
  templateUrl: './turmas-page.component.html',
  styleUrl: './turmas-page.component.css'
})
export class TurmasPageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly turmaService = inject(TurmaService);
  private readonly disciplinaService = inject(DisciplinaService);
  private readonly notify = inject(NotificationService);

  disciplinas: Disciplina[] = [];
  turmas: Turma[] = [];
  editandoId: number | null = null;

  form = this.fb.nonNullable.group({
    disciplinaId: [0, [Validators.required, Validators.min(1)]],
    codigo: ['', Validators.required],
    periodo: ['2026.1', Validators.required],
    vagasTotais: [30, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.disciplinaService.listar().subscribe(lista => {
      this.disciplinas = lista;
      if (lista.length) {
        this.form.patchValue({ disciplinaId: lista[0].id });
      }
    });
    this.carregar();
  }

  carregar(): void {
    this.turmaService.listar().subscribe(lista => (this.turmas = lista));
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const raw = this.form.getRawValue();
    if (this.editandoId) {
      this.turmaService
        .atualizar(this.editandoId, {
          codigo: raw.codigo,
          periodo: raw.periodo,
          vagasTotais: raw.vagasTotais
        })
        .subscribe(() => {
          this.notify.sucesso('Turma atualizada');
          this.limpar();
          this.carregar();
        });
      return;
    }
    this.turmaService.criar(raw).subscribe(() => {
      this.notify.sucesso('Turma cadastrada');
      this.limpar();
      this.carregar();
    });
  }

  editar(t: Turma): void {
    this.editandoId = t.id;
    this.form.setValue({
      disciplinaId: t.disciplinaId,
      codigo: t.codigo,
      periodo: t.periodo,
      vagasTotais: t.vagasTotais
    });
  }

  abrir(t: Turma): void {
    this.turmaService.abrir(t.id).subscribe(() => {
      this.notify.sucesso('Turma aberta');
      this.carregar();
    });
  }

  fechar(t: Turma): void {
    this.turmaService.fechar(t.id).subscribe(() => {
      this.notify.sucesso('Turma fechada');
      this.carregar();
    });
  }

  excluir(t: Turma): void {
    if (!confirm(`Excluir turma ${t.codigo}?`)) {
      return;
    }
    this.turmaService.excluir(t.id).subscribe(() => {
      this.notify.sucesso('Turma excluida');
      this.carregar();
    });
  }

  limpar(): void {
    this.editandoId = null;
    this.form.patchValue({
      codigo: '',
      periodo: '2026.1',
      vagasTotais: 30,
      disciplinaId: this.disciplinas[0]?.id ?? 0
    });
  }
}
