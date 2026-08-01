import { Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgFor, NgIf, NgTemplateOutlet } from '@angular/common';
import { Aluno } from '../../core/models';
import { AlunoService } from '../../services/aluno.service';
import { NotificationService } from '../../core/notification.service';
import { focarBlocoEdicao } from '../../core/edit-focus';
import { TabelaSort } from '../../core/tabela-sort';

@Component({
  selector: 'app-alunos-page',
  standalone: true,
  imports: [ReactiveFormsModule, NgFor, NgIf, NgTemplateOutlet],
  templateUrl: './alunos-page.component.html',
  styleUrl: './alunos-page.component.css'
})
export class AlunosPageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(AlunoService);
  private readonly notify = inject(NotificationService);

  @ViewChild('blocoEdicao') blocoEdicao?: ElementRef<HTMLElement>;

  readonly sort = new TabelaSort<Aluno>('id');
  alunos: Aluno[] = [];
  editandoId: number | null = null;

  get alunosOrdenados(): Aluno[] {
    return this.sort.aplicar(this.alunos);
  }

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
    setTimeout(() => focarBlocoEdicao(this.blocoEdicao?.nativeElement));
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

  ordenarPor(coluna: keyof Aluno & string): void {
    this.sort.toggle(coluna);
  }
}
