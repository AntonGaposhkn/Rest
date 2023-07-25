$(async function () {
    await getUsers();
    await newUser();
    await updateUser();
})

async function getUsers() {
    let data = $('#userData');
    data.empty();
    fetch("http://localhost:8080/api/admin/roles")
        .then(response => response.json())
        .then(roles => {
            console.log(roles);
            let roleNames = "";

            roles.reverse().forEach((role, index) => {
                if (index > 0) {
                    roleNames += " ";
                }
                roleNames += role.name.replace("ROLE_", "");
            });

            document.getElementById("roleNames").innerHTML = roleNames;
        })
    fetch("http://localhost:8080/api/admin")
        .then(response => response.json())
        .then(user => {
            console.log(user);

            document.getElementById("userEmail").innerHTML = user.email;
        })
    fetch("http://localhost:8080/api/admin/users")
        .then(response => response.json())
        .then(userData => {
            console.log(userData);
            if (userData.length > 0) {
                userData.forEach((u) => {
                    let tableFilling = `$(
                        <tr>
                            <td>${u.id}</td>
                            <td>${u.name}</td>
                            <td>${u.surname}</td>
                            <td>${u.email}</td>
                            <td>${u.roles.map(r => r.name.replace("ROLE_", ""))}</td>
                            <td>
                                <button type="button" data-userid="${u.id}" data-action="update" class="btn btn-info" 
                                data-toggle="modal" data-target="#updateModal">Update</button>
                            </td>
                            <td>
                                <button type="button" data-userid="${u.id}" data-action="delete" class="btn btn-danger" onclick="deleteUser(${u.id})">Delete</button>
                            </td>
                        </tr>
                    )`;
                    data.append(tableFilling);
                })
            }
        })
}

async function newUser() {
    const form = document.forms["userForm"];

    const roleSelect = document.getElementById('addRoles');
    roleSelect.innerHTML = '';
    fetch("http://localhost:8080/api/admin/roles/all")
        .then(response => response.json())
        .then(roles => {
            roles.forEach(role => {
                let option = document.createElement('option');
                option.value = role.id;
                option.text = role.name;
                roleSelect.appendChild(option);
            });
        });

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        let addRoles = [];
        for (let i = 0; i < form.roles.options.length; i++) {
            if (form.roles.options[i].selected) addRoles.push({
                id: form.roles.options[i].value,
                name: form.roles.options[i].text
            })
        }
        fetch("http://localhost:8080/api/admin/users", {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: form.firstName.value,
                surname: form.surname.value,
                email: form.email.value,
                password: form.password.value,
                roles: addRoles
            })
        }).then(() => {
            form.reset();
            getUsers();
            $('#usersTable').click();
        })
    })
}

$('#updateModal').on('show.bs.modal', ev => {
    let button = $(ev.relatedTarget);
    let id = button.attr('data-userid');
    updateModal(id);
})

async function updateModal(id) {
    let form = document.forms["modalUpdate"];
    let user = await fetch(`http://localhost:8080/api/admin/users/${id}`)
        .then(response => response.json());

    $('#roleUpdate').empty();
    fetch("http://localhost:8080/api/admin/roles")
        .then(response => response.json())
        .then(roles => {
            console.log(roles);
            roles.forEach(role => {
                let select = false;
                for (let i = 0; i < user.roles.length; i++) {
                    if (user.roles[i].name === role.name) {
                        select = true;
                        break;
                    }
                }
                let el = document.createElement("option");
                el.value = role.id;
                el.text = role.name;
                if (select) {
                    el.selected = true;
                }
                $('#roleUpdate')[0].appendChild(el);
            })
        });
    form.id.value = user.id;
    form.firstNameUpdate.value = user.name;
    form.lastNameUpdate.value = user.surname;
    form.userEmailUpdate.value = user.email;
    form.passwordUpdate.value = user.password;
}

async function updateUser() {
    const form = document.forms["modalUpdate"];
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        let updateRoles = [];
        for (let i = 0; i < form.roles.options.length; i++) {
            if (form.roles.options[i].selected) updateRoles.push({
                id: form.roles.options[i].value,
                name: form.roles.options[i].text
            })
        }
        fetch("http://localhost:8080/api/admin/users", {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                id: form.id.value,
                name: form.firstNameUpdate.value,
                surname: form.lastNameUpdate.value,
                email: form.userEmailUpdate.value,
                password: form.passwordUpdate.value,
                roles: updateRoles
            })
        }).then(() => {
            getUsers();
            $('#closeUpdateButton').click();
        })
    })
}

async function deleteUser(id) {
    fetch(`http://localhost:8080/api/admin/users/${id}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(() => {
        getUsers();
        $('#closeDeleteButton').click();
    })
}