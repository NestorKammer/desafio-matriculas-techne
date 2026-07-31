import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { Aluno } from '../../core/models';
import { AlunoService } from '../../services/aluno.service';
import { NotificationService } from '../../core/notification.service';

@Component({
  selector: 'app-alunos-page',
  standalone: true,
  imports: [ReactiveFormsModule, NgFor, NgIf],
  templateUrl: './alunos-page.component.html',
  styleUrl: './alunos-page.component.css'
})
export class AlunosPageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(AlunoService);
  private readonly notify = inject(NotificationService);

  alunos: Aluno[] = [];
  editandoId: number | null = null;

  form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(150)]],
    email: ['', [Validators.required, Validators.email]],
    ra: ['', [Validators.required, Validators.maxLength(30)]]
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.service.listar().subscribe(lista => (this.alunos = lista));
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const payload = this.form.getRawValue();
    const req$ = this.editandoId
      ? this.service.atualizar(this.editandoId, payload)
      : this.service.criar(payload);

    req$.subscribe(() => {
      this.notify.sucesso(this.editandoId ? 'Aluno atualizado' : 'Aluno cadastrado');
      this.limpar();
      this.carregar();
    });
  }

  editar(aluno: Aluno): void {
    this.editandoId = aluno.id;
    this.form.setValue({ nome: aluno.nome, email: aluno.email, ra: aluno.ra });
  }

  excluir(aluno: Aluno): void {
    if (!confirm(`Excluir aluno ${aluno.nome}?`)) {
      return;
    }
    this.service.excluir(aluno.id).subscribe(() => {
      this.notify.sucesso('Aluno excluido');
      this.carregar();
    });
  }

  limpar(): void {
    this.editandoId = null;
    this.form.reset({ nome: '', email: '', ra: '' });
  }
}
