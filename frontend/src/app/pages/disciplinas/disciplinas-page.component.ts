import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { Curso, Disciplina } from '../../core/models';
import { CursoService } from '../../services/curso.service';
import { DisciplinaService } from '../../services/disciplina.service';
import { NotificationService } from '../../core/notification.service';

@Component({
  selector: 'app-disciplinas-page',
  standalone: true,
  imports: [ReactiveFormsModule, NgFor, NgIf],
  templateUrl: './disciplinas-page.component.html',
  styleUrl: './disciplinas-page.component.css'
})
export class DisciplinasPageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly disciplinaService = inject(DisciplinaService);
  private readonly cursoService = inject(CursoService);
  private readonly notify = inject(NotificationService);

  cursos: Curso[] = [];
  disciplinas: Disciplina[] = [];
  editandoId: number | null = null;

  form = this.fb.nonNullable.group({
    cursoId: [0, [Validators.required, Validators.min(1)]],
    codigo: ['', Validators.required],
    nome: ['', Validators.required],
    cargaHoraria: [80, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    this.cursoService.listar().subscribe(lista => {
      this.cursos = lista;
      if (lista.length && !this.form.controls.cursoId.value) {
        this.form.patchValue({ cursoId: lista[0].id });
      }
    });
    this.carregar();
  }

  carregar(): void {
    this.disciplinaService.listar().subscribe(lista => (this.disciplinas = lista));
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const payload = this.form.getRawValue();
    const req$ = this.editandoId
      ? this.disciplinaService.atualizar(this.editandoId, payload)
      : this.disciplinaService.criar(payload);
    req$.subscribe(() => {
      this.notify.sucesso(this.editandoId ? 'Disciplina atualizada' : 'Disciplina cadastrada');
      this.limpar();
      this.carregar();
    });
  }

  editar(d: Disciplina): void {
    this.editandoId = d.id;
    this.form.setValue({
      cursoId: d.cursoId,
      codigo: d.codigo,
      nome: d.nome,
      cargaHoraria: d.cargaHoraria
    });
  }

  excluir(d: Disciplina): void {
    if (!confirm(`Excluir disciplina ${d.codigo}?`)) {
      return;
    }
    this.disciplinaService.excluir(d.id).subscribe(() => {
      this.notify.sucesso('Disciplina excluida');
      this.carregar();
    });
  }

  limpar(): void {
    this.editandoId = null;
    this.form.patchValue({
      codigo: '',
      nome: '',
      cargaHoraria: 80,
      cursoId: this.cursos[0]?.id ?? 0
    });
  }
}
