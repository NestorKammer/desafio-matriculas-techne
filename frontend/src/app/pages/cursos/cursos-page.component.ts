import { Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgFor, NgIf, NgTemplateOutlet } from '@angular/common';
import { Curso } from '../../core/models';
import { CursoService } from '../../services/curso.service';
import { NotificationService } from '../../core/notification.service';
import { focarBlocoEdicao } from '../../core/edit-focus';
import { TabelaSort } from '../../core/tabela-sort';

@Component({
  selector: 'app-cursos-page',
  standalone: true,
  imports: [ReactiveFormsModule, NgFor, NgIf, NgTemplateOutlet],
  templateUrl: './cursos-page.component.html',
  styleUrl: './cursos-page.component.css'
})
export class CursosPageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(CursoService);
  private readonly notify = inject(NotificationService);

  @ViewChild('blocoEdicao') blocoEdicao?: ElementRef<HTMLElement>;

  readonly sort = new TabelaSort<Curso>('codigo');
  cursos: Curso[] = [];
  editandoId: number | null = null;

  get cursosOrdenados(): Curso[] {
    return this.sort.aplicar(this.cursos);
  }

  form = this.fb.nonNullable.group({
    codigo: ['', Validators.required],
    nome: ['', Validators.required],
    cargaHoraria: [3600, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.service.listar().subscribe(lista => (this.cursos = lista));
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
      this.notify.sucesso(this.editandoId ? 'Curso atualizado' : 'Curso cadastrado');
      this.limpar();
      this.carregar();
    });
  }

  editar(curso: Curso): void {
    this.editandoId = curso.id;
    this.form.setValue({
      codigo: curso.codigo,
      nome: curso.nome,
      cargaHoraria: curso.cargaHoraria
    });
    setTimeout(() => focarBlocoEdicao(this.blocoEdicao?.nativeElement));
  }

  excluir(curso: Curso): void {
    if (!confirm(`Excluir curso ${curso.codigo}?`)) {
      return;
    }
    this.service.excluir(curso.id).subscribe(() => {
      this.notify.sucesso('Curso excluido');
      this.carregar();
    });
  }

  limpar(): void {
    this.editandoId = null;
    this.form.reset({ codigo: '', nome: '', cargaHoraria: 3600 });
  }

  ordenarPor(coluna: keyof Curso & string): void {
    this.sort.toggle(coluna);
  }
}
