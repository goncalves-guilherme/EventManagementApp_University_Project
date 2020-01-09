using System;
using Microsoft.AspNetCore.Identity;
using SilentManager.API.Models.DB;
using Microsoft.EntityFrameworkCore;
using SilentManager.API.Models;

namespace SilentManager.API.Security
{
    public class IdentityInitializer
    {
        private readonly DbContext _context;
        private readonly UserManager<Userl> _userManager;
        private readonly RoleManager<IdentityRole> _roleManager;

        public IdentityInitializer(
            DbContext context,
            UserManager<Userl> userManager,
            RoleManager<IdentityRole> roleManager)
        {
            _context = context;
            _userManager = userManager;
            _roleManager = roleManager;
        }

        public void Initialize()
        {
            if (_context.Database.EnsureCreated())
            {
                if (!_roleManager.RoleExistsAsync(Roles.ROLE_API_PRODUTOS).Result)
                {
                    var resultado = _roleManager.CreateAsync(
                        new IdentityRole(Roles.ROLE_API_PRODUTOS)).Result;
                    if (!resultado.Succeeded)
                    {
                        throw new Exception(
                            $"Erro durante a criação da role {Roles.ROLE_API_PRODUTOS}.");
                    }
                }

                CreateUser(
                    new Userl()
                    {
                        UserName = "admin_apiprodutos",
                        Email = "admin-apiprodutos@teste.com.br",
                        EmailConfirmed = true
                    }, "AdminAPIProdutos01!", Roles.ROLE_API_PRODUTOS);

                CreateUser(
                    new Userl()
                    {
                        UserName = "usrinvalido_apiprodutos",
                        Email = "usrinvalido-apiprodutos@teste.com.br",
                        EmailConfirmed = true
                    }, "UsrInvAPIProdutos01!");
            }
        }
        private void CreateUser(
            Userl user,
            string password,
            string initialRole = null)
        {
            if (_userManager.FindByNameAsync(user.UserName).Result == null)
            {
                var resultado = _userManager
                    .CreateAsync(user, password).Result;

                if (resultado.Succeeded &&
                    !String.IsNullOrWhiteSpace(initialRole))
                {
                    _userManager.AddToRoleAsync(user, initialRole).Wait();
                }
            }
        }
    }
}