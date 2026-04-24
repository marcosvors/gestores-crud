const API = '/gestores';

const form = document.getElementById('form-gestor');
const campoId = document.getElementById('gestor-id');
const campoNome = document.getElementById('nome');
const campoEmail = document.getElementById('email');
const formTitulo = document.getElementById('form-titulo');
const btnSalvar = document.getElementById('btn-salvar');
const btnCancelar = document.getElementById('btn-cancelar');
const corpoTabela = document.getElementById('corpo-tabela');
const linhaVazia = document.getElementById('linha-vazia');
const divMensagem = document.getElementById('mensagem');

let timerMensagem = null;

function mostrarMensagem(texto, tipo) {
  clearTimeout(timerMensagem);
  divMensagem.textContent = texto;
  divMensagem.className = `mensagem ${tipo}`;
  timerMensagem = setTimeout(() => {
    divMensagem.className = 'mensagem hidden';
  }, 4000);
}

async function listarGestores() {
  try {
    const res = await fetch(API);
    if (!res.ok) throw new Error('Erro ao carregar gestores.');
    const gestores = await res.json();
    renderizarTabela(gestores);
  } catch (err) {
    mostrarMensagem(err.message, 'erro');
  }
}

function renderizarTabela(gestores) {
  const linhas = corpoTabela.querySelectorAll('tr:not(#linha-vazia)');
  linhas.forEach(l => l.remove());

  if (gestores.length === 0) {
    linhaVazia.classList.remove('hidden');
    return;
  }

  linhaVazia.classList.add('hidden');

  gestores.forEach(g => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${g.id}</td>
      <td>${escapar(g.nome)}</td>
      <td>${escapar(g.email)}</td>
      <td>
        <div class="acoes-tabela">
          <button class="btn btn-editar" data-id="${g.id}">Editar</button>
          <button class="btn btn-excluir" data-id="${g.id}">Excluir</button>
        </div>
      </td>
    `;
    tr.querySelector('.btn-editar').addEventListener('click', () => carregarEdicao(g));
    tr.querySelector('.btn-excluir').addEventListener('click', () => excluirGestor(g.id));
    corpoTabela.appendChild(tr);
  });
}

function escapar(str) {
  const div = document.createElement('div');
  div.textContent = str;
  return div.innerHTML;
}

function carregarEdicao(gestor) {
  campoId.value = gestor.id;
  campoNome.value = gestor.nome;
  campoEmail.value = gestor.email;
  formTitulo.textContent = 'Editar Gestor';
  btnSalvar.textContent = 'Atualizar';
  btnCancelar.classList.remove('hidden');
  campoNome.focus();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function cancelarEdicao() {
  form.reset();
  campoId.value = '';
  formTitulo.textContent = 'Novo Gestor';
  btnSalvar.textContent = 'Salvar';
  btnCancelar.classList.add('hidden');
}

async function salvarGestor(e) {
  e.preventDefault();

  const id = campoId.value;
  const body = { nome: campoNome.value.trim(), email: campoEmail.value.trim() };

  try {
    let res;
    if (id) {
      res = await fetch(`${API}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });
    } else {
      res = await fetch(API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });
    }

    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || `Erro ${res.status}`);
    }

    mostrarMensagem(id ? 'Gestor atualizado com sucesso!' : 'Gestor criado com sucesso!', 'sucesso');
    cancelarEdicao();
    listarGestores();
  } catch (err) {
    mostrarMensagem(err.message, 'erro');
  }
}

async function excluirGestor(id) {
  if (!confirm('Deseja realmente excluir este gestor?')) return;

  try {
    const res = await fetch(`${API}/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error(`Erro ${res.status} ao excluir.`);
    mostrarMensagem('Gestor excluído com sucesso!', 'sucesso');
    listarGestores();
  } catch (err) {
    mostrarMensagem(err.message, 'erro');
  }
}

form.addEventListener('submit', salvarGestor);
btnCancelar.addEventListener('click', cancelarEdicao);

listarGestores();
